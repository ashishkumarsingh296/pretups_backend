package com.restapi.networkadmin.networkproductmap;


import com.btsl.common.BTSLBaseException;
import com.btsl.common.MasterErrorList;
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
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.commissionprofile.BatchCommissionProfileController;
import com.restapi.networkadmin.networkproductmap.requestVO.NetworkProductMappingRequestVO;
import com.restapi.networkadmin.networkproductmap.responseVO.NetworkProductMappingResponseVO;
import com.restapi.networkadmin.networkproductmap.service.NetworkProductMappingServiceI;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${NetworkProductMappingController.name}", description = "${NetworkProductMappingController.desc}")//@Api(tags = "Network Admin", defaultValue = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin/networkproductmap")
public class NetworkProductMappingController {

    public static final Log log = LogFactory.getLog(BatchCommissionProfileController.class.getName());
    public static final String classname = "NetworkProductMappingController";

    @Autowired
    NetworkProductMappingServiceI networkProductMappingServiceI;

    /**
     *
     * @param headers
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/loadnetworkproductmappingdetails", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "load the list of network product Mapping Detail", response = NetworkProductMappingResponseVO.class, authorizations = {
            @Authorization(value = "Authorization") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BatchAddCommisionProfileResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found") })
    */
    @io.swagger.v3.oas.annotations.Operation(summary = "${loadnetworkproductmappingdetails.summary}", description="${loadnetworkproductmappingdetails.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkProductMappingResponseVO.class))
                            )
                    }

                    ),


                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    })
            }
    )

    public NetworkProductMappingResponseVO loadNetworkProductMappingDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        if (log.isDebugEnabled()) {
            log.debug("loadNetworkProductMappingDetails", "Entered");
        }
        final String METHOD_NAME = "loadNetworkProductMappingDetails";
        NetworkProductMappingResponseVO response = null;
        response = new NetworkProductMappingResponseVO();

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
            response = networkProductMappingServiceI.loadNetworkProductDetails(con, locale, loginUserID, httpServletRequest, httpServletResponse);

        } catch (BTSLBaseException be) {
            log.error("", "Exception=" + be);
            log.errorTrace(METHOD_NAME, be);
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
        }
        catch (Exception e) {
            log.error(METHOD_NAME, "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            response.setStatus((HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.LOAD_NETWORK_PRODUCT_MAPPING_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LOAD_NETWORK_PRODUCT_MAPPING_FAIL);
            httpServletResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
        }finally {
            if (mcomCon != null) {
                mcomCon.close(METHOD_NAME);
                mcomCon = null;
            }
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting" + METHOD_NAME);
            }
        }
        return response;
    }

    /**
     *
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/addnetworkproductmappingdetails", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@ApiOperation(value = "add the list of network product Mapping Detail", response = NetworkProductMappingResponseVO.class, authorizations = {
            @Authorization(value = "Authorization") })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = BatchAddCommisionProfileResponseVO.class),
            @ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Not Found") })
    */
    @io.swagger.v3.oas.annotations.Operation(summary = "${addnetworkproductmappingdetails.summary}", description="${addnetworkproductmappingdetails.description}",

            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = NetworkProductMappingResponseVO.class))
                            )
                    }

                    ),


                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    }),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
                            @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
                            )
                    })
            }
    )

    public NetworkProductMappingResponseVO addNetworkProductMappingDetails(
            @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
            HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest, @RequestBody NetworkProductMappingRequestVO request){
        final String METHOD_NAME = "addNetworkProductMappingDetails";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered ");
        }

        NetworkProductMappingResponseVO response = null;
        NetworkProductMappingRequestVO networkProductMappingRequestVO = request;

        ArrayList<MasterErrorList> inputValidations = new ArrayList();
        Connection con = null;
        MComConnectionI mcomCon = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String loginID = null;
        boolean isflag = true;

        try {

            /*
             * Authentication
             *
             * @throws BTSLBaseException
             */

            response = new NetworkProductMappingResponseVO();
            OAuthUser OAuthUserData = new OAuthUser();
            OAuthUserData.setData(new OAuthUserData());
            OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, httpServletResponse);
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            loginID = OAuthUserData.getData().getLoginid();
            response = networkProductMappingServiceI.addNetworkProductMappingDetails(con, mcomCon, locale, loginID, httpServletRequest, httpServletResponse, request);

        }catch (BTSLBaseException be) {
            log.error(METHOD_NAME, "Exception:e=" + be);
            log.errorTrace(METHOD_NAME, be);
            if (!BTSLUtil.isNullString(be.getMessage())) {
                String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
                response.setMessageCode(be.getMessage());
                response.setMessage(msg);
                response.setStatus(org.apache.http.HttpStatus.SC_BAD_REQUEST);
                httpServletResponse.setStatus(org.apache.http.HttpStatus.SC_BAD_REQUEST);
            }

        }
        catch (Exception e) {
            log.error(METHOD_NAME, "Exception:e=" + e);
            log.errorTrace(METHOD_NAME, e);
            response.setStatus((org.apache.http.HttpStatus.SC_BAD_REQUEST));
            String resmsg = RestAPIStringParser.getMessage(
                    new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                    PretupsErrorCodesI.ADD_NETWORK_PRODUCT_MAPPING_FAIL, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.ADD_NETWORK_PRODUCT_MAPPING_FAIL);
            httpServletResponse.setStatus(org.apache.http.HttpStatus.SC_BAD_REQUEST);
        }

        finally {
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

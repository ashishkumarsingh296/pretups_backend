package com.restapi.networkadmin.loanmanagment;

import java.sql.Connection;
import java.util.Arrays;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
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
import com.restapi.networkadmin.loanmanagment.LoanListResponseVO;
import com.restapi.networkadmin.loanmanagment.LoanProductListResponseVO;
import com.restapi.networkadmin.loanmanagment.ModifyLoanProfileRequestVO;
import com.restapi.networkadmin.loanmanagment.AddLoanProfileRequestVO;

@Tag(name = "${LoanMainController.name}", description = "${LoanMainController.desc}")
@RestController
@RequestMapping(value = "/v1/superadmin")
@CrossOrigin
public class LoanMainController {

        public static final Log log = LogFactory.getLog(LoanMainController.class.getName());
        public static final String classname = "LoanMainController";

        @Autowired
        LoanManagmentServiceImpl loanManagmentServiceImpl;

        @Autowired
        static OperatorUtil operatorUtil = null;


        @GetMapping("/loadLoanProfileList")
        @ResponseBody
        @Operation(summary = "${loanMainController.getLoanProfileList.name}", description = "${loanMainController.getLoanProfileList.desc}",
        responses = {
                        @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LoanListResponseVO.class))) }

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
        public LoanListResponseVO getLoanProfileList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,HttpServletResponse response1, HttpServletRequest httpServletRequest,@RequestParam(name="domainCode")String domainCode,@RequestParam(name="networkName")String networkName,@RequestParam(name="categoryCode")String categoryCode)throws Exception {
                LoanListResponseVO loanListResponseVO = null;
                final String methodName = "getLoanProfileList";
                if(log.isDebugEnabled()) {
                        log.debug(methodName, "Entered : ");
                }
                LoanListResponseVO response = new LoanListResponseVO();
                Connection con = null;
                MComConnectionI mcomCon = null;
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
                String loginID = null;
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
                        loginID = OAuthUserData.getData().getLoginid();
                        response = loanManagmentServiceImpl.loadLoanProfileList(con,networkName,categoryCode,domainCode,response1);


                }catch(BTSLBaseException btslBaseException) {
                        log.error("", "Exceptin:e=" + btslBaseException);
                        log.errorTrace(methodName, btslBaseException);
                        String msg = RestAPIStringParser.getMessage(locale, btslBaseException.getMessageKey(), null);
                        response.setMessageCode(btslBaseException.getMessageKey());
                        response.setMessage(msg);

                        if (Arrays.asList(PretupsI.OAUTHCODES).contains(btslBaseException.getMessage())) {
                                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                        } else {
                                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                }catch(Exception exception) {
                        log.error("", "Exceptin:e=" + exception);
                        log.errorTrace(methodName, exception);
                        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_FAIL, null);
                        response.setMessageCode(PretupsErrorCodesI.LOAN_FAIL);
                        response.setMessage(msg);
                }
                finally {

                        if (mcomCon != null) {
                                mcomCon.close("getLoanProfileList");
                                mcomCon = null;
                        }
                        if (log.isDebugEnabled()) {
                                log.debug(methodName, "Exiting:=" + methodName);
                        }

                }

                return response;

        }

        @GetMapping("/loadLoanProfileSlabList")
        @ResponseBody
        @Operation(summary = "${loanMainController.viewLoanProfileByID.name}", description = "${loanMainController.viewLoanProfileByID.desc}",
        responses = {
                        @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LoanListResponseVO.class))) }

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
        public LoanListResponseVO viewLoanProfileByID(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> 
        headers,HttpServletResponse response1, HttpServletRequest httpServletRequest,@RequestParam(name="profileID")String profileID)throws Exception {
                LoanListResponseVO loanListResponseVO = null;
                final String methodName = "viewLoanProfileByID";
                if(log.isDebugEnabled()) {
                        log.debug(methodName, "Entered : ");
                }
                LoanListResponseVO response = new LoanListResponseVO();
                Connection con = null;
                MComConnectionI mcomCon = null;
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
                String loginID = null;
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
                        loginID = OAuthUserData.getData().getLoginid();
                        response = loanManagmentServiceImpl.viewLoanProfileByID(con,profileID,response1);


                }catch(BTSLBaseException btslBaseException) {
                        log.error("", "Exceptin:e=" + btslBaseException);
                        log.errorTrace(methodName, btslBaseException);
                        String msg = RestAPIStringParser.getMessage(locale, btslBaseException.getMessageKey(), null);
                        response.setMessageCode(btslBaseException.getMessageKey());
                        response.setMessage(msg);

                        if (Arrays.asList(PretupsI.OAUTHCODES).contains(btslBaseException.getMessage())) {
                                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                        } else {
                                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                }catch(Exception exception) {
                        log.error("", "Exceptin:e=" + exception);
                        log.errorTrace(methodName, exception);
                        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_FAIL, null);
                        response.setMessageCode(PretupsErrorCodesI.LOAN_FAIL);
                        response.setMessage(msg);
                }
                finally {

                        if (mcomCon != null) {
                                mcomCon.close("viewLoanProfileByID");
                                mcomCon = null;
                        }
                        if (log.isDebugEnabled()) {
                                log.debug(methodName, "Exiting:=" + methodName);
                        }

                }

                return response;

        }
		@GetMapping("/deleteLoanProfile")
        @ResponseBody
        @Operation(summary = "${loanMainController.deleteLoanProfileByID.name}", description = "${loanMainController.deleteLoanProfileByID.desc}",
        responses = {
                        @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LoanListResponseVO.class))) }

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
        public LoanListResponseVO deleteLoanProfileByID(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,HttpServletResponse response1, HttpServletRequest httpServletRequest,@RequestParam(name="profileID")String profileId)throws Exception {
               // LoanListResponseVO loanListResponseVO = null;
                final String methodName = "deleteLoanProfileByID";
                if(log.isDebugEnabled()) {
                        log.debug(methodName, "Entered : ");
                }
                LoanListResponseVO response = new LoanListResponseVO();
                Connection con = null;
                MComConnectionI mcomCon = null;
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
                String loginID = null;
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
                        loginID = OAuthUserData.getData().getLoginid();
                        response = loanManagmentServiceImpl.deleteLoanProfileByID(con,profileId,response1);


                }catch(BTSLBaseException btslBaseException) {
                        log.error("", "Exceptin:e=" + btslBaseException);
                        log.errorTrace(methodName, btslBaseException);
                        String msg = RestAPIStringParser.getMessage(locale, btslBaseException.getMessageKey(), null);
                        response.setMessageCode(btslBaseException.getMessageKey());
                        response.setMessage(msg);

                        if (Arrays.asList(PretupsI.OAUTHCODES).contains(btslBaseException.getMessage())) {
                                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                        } else {
                                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                }catch(Exception exception) {
                        log.error("", "Exceptin:e=" + exception);
                        log.errorTrace(methodName, exception);
                        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_FAIL, null);
                        response.setMessageCode(PretupsErrorCodesI.LOAN_FAIL);
                        response.setMessage(msg);
                }
                finally {

                        if (mcomCon != null) {
                                mcomCon.close("viewLoanProfileByID");
                                mcomCon = null;
                        }
                        if (log.isDebugEnabled()) {
                                log.debug(methodName, "Exiting:=" + methodName);
                        }

                }

                return response;

        }
		@GetMapping("/productList")
        @ResponseBody
        @Operation(summary = "${loanMainController.getProductList.name}", description = "${loanMainController.getProductList.desc}",
        responses = {
                        @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LoanListResponseVO.class))) }

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
        public LoanProductListResponseVO getProductList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,HttpServletResponse response1, HttpServletRequest httpServletRequest)throws Exception {
			//LoanProductListResponseVO loanProductListResponseVO = null;
                final String methodName = "getProductList";
                if(log.isDebugEnabled()) {
                        log.debug(methodName, "Entered : ");
                }
                LoanProductListResponseVO response = new LoanProductListResponseVO();
                Connection con = null;
                MComConnectionI mcomCon = null;
                Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
                String loginID = null;
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
                        loginID = OAuthUserData.getData().getLoginid();
                        response = loanManagmentServiceImpl.viewProductList(con, loginID, response1);


                }catch(BTSLBaseException btslBaseException) {
                        log.error("", "Exceptin:e=" + btslBaseException);
                        log.errorTrace(methodName, btslBaseException);
                        String msg = RestAPIStringParser.getMessage(locale, btslBaseException.getMessageKey(), null);
                        response.setMessageCode(btslBaseException.getMessageKey());
                        response.setMessage(msg);

                        if (Arrays.asList(PretupsI.OAUTHCODES).contains(btslBaseException.getMessage())) {
                                response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
                                response.setStatus(HttpStatus.SC_UNAUTHORIZED);
                        } else {
                                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                        }
                }catch(Exception exception) {
                        log.error("", "Exceptin:e=" + exception);
                        log.errorTrace(methodName, exception);
                        String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LOAN_FAIL, null);
                        response.setMessageCode(PretupsErrorCodesI.LOAN_FAIL);
                        response.setMessage(msg);
                }
                finally {

                        if (mcomCon != null) {
                                mcomCon.close("viewProductList");
                                mcomCon = null;
                        }
                        if (log.isDebugEnabled()) {
                                log.debug(methodName, "Exiting:=" + methodName);
                        }

                }

                return response;

        }

		
		@RequestMapping(value = "/modifyloanProfileDetails", produces = MediaType.APPLICATION_JSON,method = RequestMethod.POST)
		@ResponseBody
		@Operation(summary = "${loanMainController.modifyLoanProfileDetail.name}", description = "${loanMainController.modifyLoanProfileDetail.desc}",

				responses = {
						@ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
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
		public BaseResponse modifyLoanProfileDetail(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@RequestBody ModifyLoanProfileRequestVO requestVO) throws Exception {

			final String methodName = "modifyLoanProfile";
			if(log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

			UserVO userVO = null;
			UserDAO userDAO = null;
			
			BaseResponse response = new BaseResponse();
			
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				
				OAuthUser oAuthUser = new OAuthUser();
				oAuthUser.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
				
				userVO = new UserVO();
				userDAO = new UserDAO();
				userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
				
				response = loanManagmentServiceImpl.modifyLoanProfileDetail(headers, httpServletRequest, response1, con, mcomCon, locale, userVO, response, requestVO);
			}
			catch (BTSLBaseException be) {
				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				if (!BTSLUtil.isNullString(be.getMessage())) {
					String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), be.getArgs());
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
						PretupsErrorCodesI.COMM_PRF_UPDATE_FAIL, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.COMM_PRF_UPDATE_FAIL);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			finally {
				if (mcomCon != null) {
					mcomCon.close("modifyLoanProfile");
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}
			}
			return response;
		}
		
		@PostMapping(value= "/addloanProfile", produces = MediaType.APPLICATION_JSON)	
		@ResponseBody
		@io.swagger.v3.oas.annotations.Operation(summary = "${loanMainController.addLoanProfile.name}", description="${loanMainController.addLoanProfile.desc}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponse.class))
								)
						}

						),


						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))

										 , examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.BAD_REQUEST)}
										 
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.UNAUTH)}
										 
										 )
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
								, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.NOT_FOUND)}
						
								)
						}),
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.btsl.common.BaseResponse.class))
									, examples ={@io.swagger.v3.oas.annotations.media.ExampleObject(value = com.btsl.common.BaseResponseRedoclyCommon.INTERNAL_SERVER_ERROR)}
						)})
				}
		)

		public BaseResponse addLoanProfile(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@RequestBody AddLoanProfileRequestVO addLoanProfileRequestVO) throws Exception {
			final String methodName = "addCommissionProfile";
			if(log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			Connection con = null;
			MComConnectionI mcomCon = null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

			UserVO userVO = null;
			UserDAO userDAO = null;
			
			BaseResponse response = new BaseResponse();
			
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				
				OAuthUser oAuthUser = new OAuthUser();
				oAuthUser.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);
				
				userVO = new UserVO();
				userDAO = new UserDAO();
				userVO  = userDAO.loadUsersDetails(con, oAuthUser.getData().getMsisdn());
				
				response = loanManagmentServiceImpl.addLoanProfile(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,response,addLoanProfileRequestVO);
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
						PretupsErrorCodesI.COMM_PRF_ADD_FAIL, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.COMM_PRF_ADD_FAIL);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			
			finally {
				if (mcomCon != null) {
					mcomCon.close("addLoanProfile");
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}
			}
			return response;
		}
}

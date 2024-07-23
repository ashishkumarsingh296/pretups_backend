package com.restapi.networkadmin;

import java.util.Arrays;
import java.util.Locale;


import com.btsl.common.*;
/*
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
*/
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.requestVO.AddPromoTransferReqVO;
import com.restapi.networkadmin.requestVO.DeletePromoTransferReqVO;
import com.restapi.networkadmin.requestVO.ModifyPromoTransferReqVO;
import com.restapi.networkadmin.requestVO.PromoLoadParentParamReq;
import com.restapi.networkadmin.requestVO.PromoTransferDropdownListReq;
import com.restapi.networkadmin.requestVO.SearchPromoTransferReqVO;
import com.restapi.networkadmin.responseVO.AddPromoTransferRuleRespVO;
import com.restapi.networkadmin.responseVO.DeletePromoTransferRespVO;
import com.restapi.networkadmin.responseVO.ModifyPromoTransfRuleRespVO;
import com.restapi.networkadmin.responseVO.PromoDepDropdownlistRespVO;
import com.restapi.networkadmin.responseVO.PromoLoadParentUserRespVO;
import com.restapi.networkadmin.responseVO.SearchPromoTransferRespVO;
import com.restapi.networkadmin.service.PromotionalTransfServiceImpl;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "PromoTransferRuleController.name", description = "PromoTransferRuleController.desc")//@Api(tags = "Network admin features")
@RestController
@RequestMapping(value = "/v1/networkadmin")
public class PromoTransferRuleController {

	public static final Log log = LogFactory.getLog(PromoTransferRuleController.class.getName());
	public static final String classname = "TransferProfileController";





	@Autowired
	private PromotionalTransfServiceImpl promotionalTransfRuleService;
	
		

		@GetMapping(value = "/promoTransfeRuleDropDowns", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(value = "Promotional transfer rule Dependency dropdowns", response = PromoDepDropdownlistRespVO.class, authorizations = {
				@Authorization(value = "Authorization") })
*/

		/*
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
*/


		@io.swagger.v3.oas.annotations.Operation(summary = "${promoTransfeRuleDropDowns.summary}", description="${promoTransfeRuleDropDowns.description}",

		responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
						@io.swagger.v3.oas.annotations.media.Content(
								mediaType = "application/json",
								array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PromoDepDropdownlistRespVO.class))
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


		/*@ApiResponses(value = {
				@ApiResponse(code = 200, message = "OK1", response = PromoDepDropdownlistRespVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/


		public PromoDepDropdownlistRespVO getPromoDropDownData(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
				HttpServletRequest httprequest
				) throws Exception {

		final String methodName = "promoTransfeRuleDropDowns";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "promoTransfeRuleDropDowns ");
		}
		MasterErrorList masterError=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		
		PromoDepDropdownlistRespVO response=new PromoDepDropdownlistRespVO();

		String loginID = null;
		try {
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
			PromoTransferDropdownListReq requestVO = new PromoTransferDropdownListReq();
 		   response = promotionalTransfRuleService.getPromoDependencyDropDownlist(requestVO, oAuthUserData.getData().getLoginid());
			
		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}
		
		

		
		
		
		@GetMapping(value = "/loadPromoParentUserData", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(value = "Load parent user data", response = PromoDepDropdownlistRespVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = {
				@ApiResponse(code = 200, message = "OK", response = PromoDepDropdownlistRespVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${loadPromoParentUserData.summary}", description="${loadPromoParentUserData.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PromoLoadParentUserRespVO.class))
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

		public PromoLoadParentUserRespVO loadPromoParentUserData(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
				HttpServletRequest httprequest,
				@Parameter(description = "geoDomainCode", required = true) @RequestParam("geoDomainCode") String geoDomainCode,
				@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
				@Parameter(description = "userName", required = true) @RequestParam("userName") String userName
				) throws Exception {	
		
		
		final String methodName = "loadPromoParentUserData";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "loadPromoParentUserData ");
		}
		MasterErrorList masterError=null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		
		PromoLoadParentUserRespVO response=new PromoLoadParentUserRespVO();
		
		String loginID = null;
		try {
			OAuthUser oAuthUserData = new OAuthUser();
			oAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
			PromoLoadParentParamReq requestVO = new PromoLoadParentParamReq();
			requestVO.setCategoryCode(categoryCode);
			requestVO.setGeoDomainCode(geoDomainCode);
			requestVO.setUserName(userName);
			response = promotionalTransfRuleService.loadParentUserList(requestVO, oAuthUserData.getData().getLoginid());
			
		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} finally {

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;
	}
		

		
		
		
		
		

		@PostMapping(value = "/searchPromoTransferData", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(value = "Search promo transfer data", response = SearchPromoTransferRespVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = SearchPromoTransferRespVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${searchPromoTransferData.summary}", description="${searchPromoTransferData.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SearchPromoTransferRespVO.class))
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

		public SearchPromoTransferRespVO searchPromoTransferData(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@Parameter(description = "PROMO_TRANSFER_PROFILE_SEARCH", required = true) @RequestBody SearchPromoTransferReqVO requestVO)
				throws Exception {
			
			final String methodName = "searchPromoTransferData";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "searchPromoTransferData ");
			}
			MasterErrorList masterError=null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			
			SearchPromoTransferRespVO response=new SearchPromoTransferRespVO();

			String loginID = null;
			try {
				OAuthUser oAuthUserData = new OAuthUser();
				oAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
				response = promotionalTransfRuleService.searchPromoTransferData(requestVO, oAuthUserData.getData().getLoginid());
			} catch (BTSLBaseException be) {
				log.error("", "Exceptin:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} finally {

				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}

			}
			return response;
		}

		



		@PostMapping(value = "/addPromoTransferData", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(value = "Add promo transfer data", response = AddPromoTransferRuleRespVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = AddPromoTransferRuleRespVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${addPromoTransferData.summary}", description="${addPromoTransferData.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AddPromoTransferRuleRespVO.class))
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

		public AddPromoTransferRuleRespVO addPromoTransferData(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@Parameter(description = "PROMO_TRANSFER_PROFILE_ADD", required = true) @RequestBody AddPromoTransferReqVO requestVO)
				throws Exception {
			
			final String methodName = "addPromoTransferData";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "addPromoTransferData request reached  ");
			}
			MasterErrorList masterError=null;
			Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			AddPromoTransferRuleRespVO response=new AddPromoTransferRuleRespVO();
			String loginID = null;
			try {
				OAuthUser oAuthUserData = new OAuthUser();
				oAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
				response = promotionalTransfRuleService.addPromoTransferData(requestVO, oAuthUserData.getData().getLoginid(),locale);
				
			} catch (BTSLBaseException be) {
				log.error("", "Exceptin:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} finally {

				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}

			}
			return response;
		}
		
		
		
		
		
		
		
		
		
		@PostMapping(value = "/updatePromoTransferData", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(value = "Modify promo transfer data", response = ModifyPromoTransfRuleRespVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ModifyPromoTransfRuleRespVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${updatePromoTransferData.summary}", description="${updatePromoTransferData.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ModifyPromoTransfRuleRespVO.class))
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

		public ModifyPromoTransfRuleRespVO updatePromoTransferData(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@Parameter(description = "PROMO_TRANSFER_PROFILE_MODIFY", required = true) @RequestBody ModifyPromoTransferReqVO requestVO)
				throws Exception {
			
			final String methodName = "updatePromoTransferData";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "updatePromoTransferData request reached  ");
			}
			MasterErrorList masterError=null;
			//Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			Locale locale = LocaleContextHolder.getLocale();
			ModifyPromoTransfRuleRespVO response=new ModifyPromoTransfRuleRespVO();
			String loginID = null;
			try {
				OAuthUser oAuthUserData = new OAuthUser();
				oAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
				response = promotionalTransfRuleService.modifyPromoTransferData(requestVO, oAuthUserData.getData().getLoginid(),locale);
				
			} catch (BTSLBaseException be) {
				log.error("", "Exceptin:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} finally {

				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}

			}
			return response;
		}

		
		
		
		@PostMapping(value = "/deletePromoTransferData", produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation(value = "Delete promo transfer data", response = DeletePromoTransferRespVO.class, authorizations = {
				@Authorization(value = "Authorization") })
		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = DeletePromoTransferRespVO.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${deletePromoTransferData.summary}", description="${deletePromoTransferData.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DeletePromoTransferRespVO.class))
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

		public DeletePromoTransferRespVO deletePromoTransferData(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httpServletRequest,
				@Parameter(description = "PROMO_TRANSFER_PROFILE_DELETE", required = true) @RequestBody DeletePromoTransferReqVO requestVO)
				throws Exception {
			
			final String methodName = "deletePromoTransferData";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "deletePromoTransferData request reached  ");
			}
			MasterErrorList masterError=null;
			//Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			Locale locale = LocaleContextHolder.getLocale();
			DeletePromoTransferRespVO response=new DeletePromoTransferRespVO();
			String loginID = null;
			try {
				OAuthUser oAuthUserData = new OAuthUser();
				oAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(oAuthUserData, headers, response1);
				response = promotionalTransfRuleService.deletePromoTransferData(requestVO, oAuthUserData.getData().getLoginid(),locale);
				
			} catch (BTSLBaseException be) {
				log.error("", "Exceptin:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
			} finally {

				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting:=" + methodName);
				}

			}
			return response;
		}

		
		
		
		
}

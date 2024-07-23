package com.restapi.channeluser.service;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseRedoclyCommon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchAdminRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AreaSearchResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.BatchUserInitiateResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.ProfileListResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.SAPResponseVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.xl.UserHierarchyExcelRW;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.users.logiid.LoginIdResponseVO;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
import com.web.pretups.channel.user.web.ChannelUserTransferForm;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;


import io.swagger.v3.oas.annotations.Parameter;



import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelUserServicesController.name}", description = "${ChannelUserServicesController.desc}")//@Api(tags="Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class ChannelUserServicesController {

	public static final Log log = LogFactory.getLog(UserThresholdController.class.getName());
	
	@Autowired
	ChannelUserServicesI channelUserServicesImpl;
	
	 @GetMapping(value ="/areasearch", produces = MediaType.APPLICATION_JSON)
	 @ResponseBody
	 /*@ApiOperation(tags="Channel Users", value = "Search Area",response=AreaSearchResponseVO.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
	 @ApiResponses(value = {
		        @ApiResponse(code = 200, message = "OK", response = AreaSearchResponseVO.class),
		        @ApiResponse(code = 400, message = "Bad Request" ),
		        @ApiResponse(code = 401, message = "Unauthorized"),
		        @ApiResponse(code = 404, message = "Not Found") })
	 */

	 @io.swagger.v3.oas.annotations.Operation(summary = "${areasearch.summary}", description="${areasearch.description}",

			 responses = {
					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AreaSearchResponseVO.class))
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

	 public AreaSearchResponseVO processAreaSearch(
			 
			 	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httprequest,
				@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
				@Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
				@Parameter(description = "geoDomainCode", required = true) @RequestParam("geoDomainCode") String geoDomainCode,
				@Parameter(description = "parentLoginId", required = true) @RequestParam("parentLoginId") String parentLoginId,
				@Parameter(description = "requestType", required = true) @RequestParam("requestType") String requestType

		) {
		 

	    	final String methodName = "processAreaSearch";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			Connection con = null;
			MComConnectionI mcomCon = null;
			AreaSearchResponseVO response = new AreaSearchResponseVO();
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			CategoryVO userCategoryVO = null;
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				OAuthUser OAuthUserData = new OAuthUser();
				OAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
				String loginId = OAuthUserData.getData().getLoginid();
				
				AreaSearchRequestVO requestVO = new AreaSearchRequestVO(domainCode, categoryCode, geoDomainCode, parentLoginId, requestType);
				
				response = channelUserServicesImpl.searchArea(loginId, con, requestVO, response1);
				
				final List userCatList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, categoryCode);
	            userCategoryVO = (CategoryVO) userCatList.get(0);
	            
	            response.setOutletAllow(userCategoryVO.getOutletsAllowed());
				
			}catch(BTSLBaseException be) {
				log.error("processRequest", "Exception:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);
				
				if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(401);
				}else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(400);
				}
			}catch(Exception e) {
				log.error("processRequest", "Exception:e=" + e);
				log.errorTrace(methodName, e);
				
				response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			}finally {
				if(mcomCon != null) {
					mcomCon.close("StaffUserController#"+methodName);
					mcomCon=null;
				}
				if(log.isDebugEnabled()) {
					log.debug(methodName, "Exited");
				}
				try {
					if(con != null) {
						con.close();
					}
				}catch(Exception e) {
						log.errorTrace(methodName, e);
					}
				}
			
			return response;
		}
	 
	 @GetMapping(value ="/areasearchadmin", produces = MediaType.APPLICATION_JSON)
	 @ResponseBody
	 /*@ApiOperation(tags="Channel Users", value = "Search Area for channel user.",response=AreaSearchResponseVO.class,
			 notes = ("Api Info:") + ("\n") + ("1. Only channel admin is allowed to use the api") + ("\n") + 
				("2. Case add: If the user category is top of hierarchy then Geo Domain is required else parent login id is required.Category code and domain are required always.") + ("\n") + 
				 ("3. Case Edit: Only search login id is required.")+ ("\n"),
				authorizations = {
	    	            @Authorization(value = "Authorization")})
	 @ApiResponses(value = {
		        @ApiResponse(code = 200, message = "OK", response = AreaSearchResponseVO.class),
		        @ApiResponse(code = 400, message = "Bad Request" ),
		        @ApiResponse(code = 401, message = "Unauthorized"),
		        @ApiResponse(code = 404, message = "Not Found") })
	 */
	 @io.swagger.v3.oas.annotations.Operation(summary = "${areasearchadmin.summary}", description="${areasearchadmin.description}",

			 responses = {
					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AreaSearchResponseVO.class))
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


	 public AreaSearchResponseVO processAreaSearchAdmin(
			 
			 	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httprequest,
				@Parameter(description = "domainCode", required = false) @RequestParam("domainCode") Optional<String> domainCode,
				@Parameter(description = "categoryCode", required = false) @RequestParam("categoryCode") Optional<String> categoryCode,
				@Parameter(description = "geoDomainCode", required = false) @RequestParam("geoDomainCode") Optional<String> geoDomainCode,
				@Parameter(description = "parentLoginId", required = false) @RequestParam("parentLoginId") Optional<String> parentLoginId,
				@Parameter(description = "searchLoginId", required = false) @RequestParam("searchLoginId") Optional<String> searchLoginId,
				@Parameter(description = "requestType", required = true) @RequestParam("requestType") String requestType

		) {
		 

	    	final String methodName = "processAreaSearchAdmin";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			Connection con = null;
			MComConnectionI mcomCon = null;
			AreaSearchResponseVO response = new AreaSearchResponseVO();
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			CategoryVO userCategoryVO = null;
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				OAuthUser OAuthUserData = new OAuthUser();
				OAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
				String loginId = OAuthUserData.getData().getLoginid();
				String domainCodeString = domainCode.map(Object::toString).orElse(null);
				String categoryCodeString = categoryCode.map(Object::toString).orElse(null);
				String geoDomainCodeString = geoDomainCode.map(Object::toString).orElse(null);
				String parentLoginIdString = parentLoginId.map(Object::toString).orElse(null);
				String searchLoginIdString = searchLoginId.map(Object::toString).orElse(null);
				AreaSearchAdminRequestVO requestVO = new AreaSearchAdminRequestVO(domainCodeString, categoryCodeString, geoDomainCodeString, parentLoginIdString, requestType); 
				requestVO.setSearchLoginId(searchLoginIdString);
				response = channelUserServicesImpl.searchAreaAdmin(loginId, con, requestVO, response1);
			}catch(BTSLBaseException be) {
				log.error("processAreaSearchAdmin", "Exception:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);
				
				if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(401);
				}else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(400);
				}
			}catch(Exception e) {
				log.error("processAreaSearchAdmin", "Exception:e=" + e);
				log.errorTrace(methodName, e);
				
				response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			}finally {
				if(mcomCon != null) {
					mcomCon.close("ChannelUserServicesController#"+methodName);
					mcomCon=null;
				}
				if(log.isDebugEnabled()) {
					log.debug(methodName, "Exited");
				}
				try {
					if(con != null) {
						con.close();
					}
				}catch(Exception e) {
						log.errorTrace(methodName, e);
					}
				}
			
			return response;
		}
	 
	 @GetMapping(value ="/fetchUserData", produces = MediaType.APPLICATION_JSON)
	 @ResponseBody
	 @Operation(summary = "${ChannelUserServicesController.fetchSAPData.name}", description = "${ChannelUserServicesController.fetchSAPData.desc}",
			 responses = {
					 @ApiResponse(responseCode =Constants.API_SUCCESS_RESPONSE_CODE, description = Constants.API_SUCCESS_RESPONSE_DESC, content = {
							 @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SAPResponseVO.class))) }

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
	 public SAPResponseVO fetchSAPData(
			 
			 	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1, HttpServletRequest httprequest,
					@Parameter(description = "extCode", required = true) @RequestParam("extCode") String extCode,
				@Parameter(description = "network", required = true) @RequestParam("network") String network)
	 {
		 SAPResponseVO responseVO = new SAPResponseVO();
		 
		 responseVO = channelUserServicesImpl.fetchUserData(network, extCode, response1);
		 
		 return responseVO;
	 }
	 
	 /**
	  * 
	  * @param userId
	  * @param headers
	  * @param responseLoginId
	  * @return
	  */
		@GetMapping("/verification/userId")

		/*@ApiOperation(response = LoginIdResponseVO.class, authorizations = {
				@Authorization(value = "Authorization") }, value = "Authorization")

		@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = BaseResponseMultiple.class),
				@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${verification.summary}", description="${verification.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = LoginIdResponseVO.class))
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


		public LoginIdResponseVO getLoginID(@RequestParam String userId,
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse responseLoginId) {

			final String methodName = "userNameEntered";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered");
			}
			OAuthUser oAuthUser = null;
			MComConnectionI mcomCon = null;
			LoginIdResponseVO response = new LoginIdResponseVO();
			Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
			try {
				
				mcomCon = new MComConnection();
				Connection con = mcomCon.getConnection();
				 
	            /*
				 * Authentication
				 * @throws BTSLBaseException
				 */
				
				oAuthUser = new OAuthUser();
				oAuthUser.setData(new OAuthUserData());
				response = channelUserServicesImpl.getLoginID(con, userId);

				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseLoginId);

			} catch (BTSLBaseException be) {

				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					responseLoginId.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus((HttpStatus.SC_UNAUTHORIZED));
				} else {
					responseLoginId.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus((HttpStatus.SC_BAD_REQUEST));
				}

			} catch (Exception e) {
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace(methodName, e);

				response.setLoginIdExist(false);

				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
						PretupsErrorCodesI.USER_UNAUTHORIZED, null);

				response.setMessage(resmsg);

			} finally {

				if (mcomCon != null) {
					mcomCon.close("LoginIdController" + methodName);
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug("userNameEntered", " Exited ");
				}

			}
			return response;
		}
		
		@PostMapping(value = "/batchUserInitiate", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
		@ResponseBody
	    /*@ApiOperation( value = "BatchUserInitiate",response = BatchUserInitiateResponseVO.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = BatchUserInitiateResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${batchUserInitiate.summary}", description="${batchUserInitiate.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BatchUserInitiateResponseVO.class))
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



	    public BatchUserInitiateResponseVO processBatchUserInitiate( 
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse response1,HttpServletRequest httprequest,
				@RequestBody BatchUserInitiateRequestVO batchUserInitiateRequestVO ) throws Exception {
		    final String methodName = "processBatchUserInitiate";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}

			BatchUserInitiateResponseVO response = new BatchUserInitiateResponseVO();
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			Connection con = null;
			MComConnectionI mcomCon = null;
			
			try 
			{
				
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				OAuthUser OAuthUserData = new OAuthUser();
				OAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
				String loginId = OAuthUserData.getData().getLoginid();
				
				response = channelUserServicesImpl.batchUserInitiateProcess(loginId, batchUserInitiateRequestVO, response1);
			}
			catch(BTSLBaseException be){
		        log.error("processBatchO2CBatchProcess", "Exceptin:e=" + be);
		        log.errorTrace(methodName, be);
	       	    String msg=RestAPIStringParser.getMessage(locale, be.getMessageKey(),null);
		        response.setMessageCode(be.getMessageKey());
		        response.setMessage(msg);

	        	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
	        		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
	    	         response.setStatus("401");
	            }
	           else{
	        	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	           		response.setStatus("400");
	           }
	        }finally {

	            // as to make the status of the batch o2c process as complete into
	            // the table so that only
	            // one instance should be executed for batch o2c

	            if (mcomCon != null) {
					mcomCon.close("O2CBatchProcessController#processBatchO2CBatchProcess");
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(methodName, "Exiting:=" + methodName);
	            }
	        
	        }
	        return response;
	    }
		
		
		

		/**
		 * @author sarthak.saini
		 * @param headers
		 * @param userDomain
		 * @param parentCategory
		 * @param geography
		 * @param userCategory
		 * @param status
		 * @param responseSwag
		 * @return
		 */
		
		@PostMapping(value= "/viewUserHierarchy", consumes = MediaType.APPLICATION_JSON,produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation( value = "Fetch User Hierarchy",response = UserHierarchyResponseVO.class,
				notes = ("Api Info:") + ("\n") + 
				 ("1. Enter either LoginId or Msisdn") + ("\n") + 
				 ("2. While searching via other parameters keep loginId/msisdn blank.")+ ("\n"),
						
				 authorizations = {
	    	            @Authorization(value = "Authorization")})
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = UserHierarchyResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/

		@io.swagger.v3.oas.annotations.Operation(summary = "${viewUserHierarchy.summary}", description="${viewUserHierarchy.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserHierarchyResponseVO.class))
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

		public UserHierarchyResponseVO viewUserHierarchy(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				 @Parameter(description = "userDomain", required = true)
		 		 @RequestBody UserHierarchyRequestVO userHierarchyRequestVO,
				 HttpServletResponse responseSwag) {
			final String methodName = "viewUserHierarchy";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			  UserHierarchyResponseVO response =null;
			  ArrayList<ChannelUserVO> userList= null;
			  UserDAO userDAO = null;
			  UserVO userVO =null;
			  ChannelUserVO channelUserVO =null;
			  ChannelUserVO channelUserHierarchyVO =null;
			  
			  Connection con = null;
			  MComConnectionI mcomCon = null;
			  ChannelUserWebDAO channelUserWebDAO = null;
			  String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	          String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	          final ChannelUserTransferForm theForm = new ChannelUserTransferForm();
	          try {
	  	    	response= new UserHierarchyResponseVO();
	  	    	mcomCon = new MComConnection();
	  	        con=mcomCon.getConnection();
	  	        String loginID = null;
	  	        channelUserWebDAO = new ChannelUserWebDAO();
	  	        userDAO = new UserDAO();
	  	        OAuthUser oAuthUserData=new OAuthUser();
	  	        oAuthUserData.setData(new OAuthUserData());
	  	        OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,responseSwag);
	  	        ArrayList<ChannelUserVO> hierarchyList= null;
	  	        
	  	         String status1=userHierarchyRequestVO.getStatus();
	  	         
	  	         if(BTSLUtil.isNullorEmpty( userHierarchyRequestVO.getMsisdn()))
	  	        	 userVO = userDAO.loadAllUserDetailsByLoginID(con,  oAuthUserData.getData().getLoginid());
	  	         
	  	         channelUserVO = new ChannelUserVO();
	  	         
	  	         if(!BTSLUtil.isNullorEmpty( userHierarchyRequestVO.getLoginId()))
	  	        	 channelUserVO =(ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, userHierarchyRequestVO.getLoginId());
	  	         else if (!BTSLUtil.isNullorEmpty( userHierarchyRequestVO.getMsisdn()))
	  	        	loginID = oAuthUserData.getData().getLoginid();
	  	         else
	  	        	 channelUserVO =(ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con,  oAuthUserData.getData().getLoginid());

	  	   
	           // if user entered the mobile number then we perform the various
	           // validations as
	           // 1. if channel user login then it should be in its user hierarchy
	           // 2. if channel admin login then it should be in its allowed
	           // geographical domains and channel domains.
	  	      if( !BTSLUtil.isNullorEmpty( userHierarchyRequestVO.getLoginId())) {
	  	    	channelUserHierarchyVO =new ChannelUserVO();
	  	    	 if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
	                   String userID = null;
	                   if (PretupsI.CATEGORY_TYPE_AGENT.equals(userVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(userVO.getCategoryVO().getHierarchyAllowed())) {
	                       userID = userVO.getParentID();
	                   } else {
	                       userID = userVO.getUserID();
	                   }

	                   // load whole hierarchy of the form user and check to user
	                   // under the hierarchy.

	                   hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, userID, false);
	                   if (hierarchyList == null || hierarchyList.isEmpty()) {
	                       if (log.isDebugEnabled()) {
	                           log.debug("viewUserDetails", "Logged in user has no child user so there would be no transactions");
	                       }
	                       throw new BTSLBaseException(this, "viewUserDetails", "Logged in user has no child user so there would be no transactions", "selectfromowner");
	                   		}
	                   
	                   boolean isMatched = false;
	                    if (hierarchyList != null && !hierarchyList.isEmpty()) {
	                        isMatched = false;
	                        for (int i = 0, j = hierarchyList.size(); i < j; i++) {
	                        	channelUserHierarchyVO = (ChannelUserVO) hierarchyList.get(i);
	                            if (channelUserHierarchyVO.getLoginID().equals(userHierarchyRequestVO.getLoginId())) {
	                                isMatched = true;
	                                break;
	                            }
	                        }
	                        if (!isMatched) {
	         	  	           throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID);
	                        }
	                        
	                        userList = loadUserList(channelUserVO, channelUserVO.getCategoryVO().getParentCategoryCode(), status1);
	     	               	if(userList == null|| userList.isEmpty())
	     	            	   throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.CHANNEL_USER_LIST_DOES_NOT_EXIST);

	     	               response = channelUserServicesImpl.fetchUserHierarchy(con, response,responseSwag,channelUserVO.getDomainID(),channelUserVO.getCategoryVO().getParentCategoryCode(),channelUserVO.getCategoryCode(),channelUserVO.getGeographicalAreaList().get(0).getGraphDomainCode(),userHierarchyRequestVO.getStatus(),userHierarchyRequestVO.getLoginId(),userHierarchyRequestVO.getMsisdn(),channelUserVO);
	                    }
	               	  }
	  	      }
	  	      else if( !BTSLUtil.isNullorEmpty( userHierarchyRequestVO.getMsisdn())) {
	  	    	final String msisdn = userHierarchyRequestVO.getMsisdn();
                final String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
	            	   throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.ERROR_UNSUPPORTED_NETWORK);
                }
                final String networkCode = networkPrefixVO.getNetworkCode();
                userVO = userDAO.loadUserDetailsByMsisdn(con, msisdn);
                if (userVO == null ||  networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
	            	   throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                }
 	        	 channelUserVO =(ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, loginID);

                userList = loadUserList(channelUserVO, channelUserVO.getCategoryVO().getParentCategoryCode(), status1);
	               if(userList == null|| userList.isEmpty())
	            	   throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.CHANNEL_USER_LIST_DOES_NOT_EXIST);

	            response = channelUserServicesImpl.fetchUserHierarchy(con, response,responseSwag,channelUserVO.getDomainID(),channelUserVO.getCategoryVO().getParentCategoryCode(),channelUserVO.getCategoryCode(),channelUserVO.getGeographicalAreaList().get(0).getGraphDomainCode(),userHierarchyRequestVO.getStatus(),userHierarchyRequestVO.getLoginId(),userHierarchyRequestVO.getMsisdn(),channelUserVO);
                
	  	      }
	  	      else {
	  	        
	  	           if (status1.equals(PretupsI.ALL)) {
	  	         			status1 = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
	  	           }
	  	           if(!userHierarchyRequestVO.getGeography().equals(channelUserVO.getGeographicalAreaList().get(0).getGraphDomainCode()))
	  	           throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.CCE_ERROR_USER_NOTIN_DOMAIN);

	              
	               
	               // validation 1 ends here
	             
	               if (channelUserVO == null || PretupsI.STAFF_USER_TYPE.equals(channelUserVO
	                   .getUserType())) {
	            		throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID);

	               }
	               
	               // validation 3 starts here
	               if (!userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
	                   final ArrayList domainList = channelUserVO.getDomainList();
	                   if (domainList != null && !domainList.isEmpty()) {
	                       ListValueVO listValueVO = null;
	                       boolean domainfound = false;

	                       for (int i = 0, j = domainList.size(); i < j; i++) {
	                           listValueVO = (ListValueVO) domainList.get(i);
	                           if (channelUserVO.getCategoryVO().getDomainCodeforCategory().equals(listValueVO.getValue())) {
	                               domainfound = true;
	                               channelUserVO.setDomainName(listValueVO.getLabel());
	                               break;
	                           }
	                       }
	                       if (!domainfound) {

	                    	   throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.CCE_ERROR_USER_NOTIN_DOMAIN);
	                    	   
	                       }
	                   }
	                   // now check that is user down in the geographical domain of
	                   // the loggin user or not.
	                   final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
	                   if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, userHierarchyRequestVO.getGeography(), userVO.getUserID())) {
	                	   throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.GEO_DOMAIN_TYPE_INVALID);

	                   }
	               }
	               // validation 3 ends here


	               // load the list of the users
	               userList = loadUserList(channelUserVO, channelUserVO.getCategoryVO().getParentCategoryCode(), status1);
	               if(userList == null|| userList.isEmpty())
	            	   throw new BTSLBaseException("viewUserDetails",methodName,PretupsErrorCodesI.CHANNEL_USER_LIST_DOES_NOT_EXIST);

	             
	               response = channelUserServicesImpl.fetchUserHierarchy(con, response,responseSwag,userHierarchyRequestVO.getUserDomain(),userHierarchyRequestVO.getParentCategory(),userHierarchyRequestVO.getUserCategory(),userHierarchyRequestVO.getGeography(),userHierarchyRequestVO.getStatus(), oAuthUserData.getData().getLoginid(), oAuthUserData.getData().getMsisdn(),channelUserVO);
	    	       

	  	           
	  	      	}
	  	    String fileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE;
	  	    String filePath = Constants.getProperty("DownloadUserHierarchyPath");
            try {
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error("loadDownloadFile", "Exception" + e.getMessage());
                throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "selectfromowner");
            }
            final String fileName = Constants.getProperty("DownloadUserHierarchyFileNamePrefix") + BTSLUtil.getFileNameStringFromDate(new Date()) + "."+fileType.toLowerCase();
            final UserHierarchyExcelRW excelRW = new UserHierarchyExcelRW();
            theForm.setUserHierarchyList(response.getUserHierarchyList());
            theForm.setCategoryList((ArrayList)channelUserVO.getCategoryList());
            theForm.setToParentCategoryDesc(channelUserVO.getParentCategoryName());
            theForm.setDomainCodeDesc(channelUserVO.getDomainName());
            theForm.setTransferUserCategoryDesc(channelUserVO.getTransferCategory());
            theForm.setMsisdn(userHierarchyRequestVO.getMsisdn());
            theForm.setLoginID(userHierarchyRequestVO.getLoginId());
            theForm.setZoneCodeDesc(channelUserVO.getDomainTypeCode());
            theForm.setOwnerName(channelUserVO.getOwnerName());
            theForm.setParentUserName(channelUserVO.getParentName());
            theForm.setParentCategoryDesc(channelUserVO.getParentCategoryName());
            if (PretupsI.FILE_CONTENT_TYPE_XLSX.equals(fileType)) {
            excelRW.writeMultipleExcelX(theForm, null, new Locale(lang, country), filePath + fileName);
            }
            else if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(fileType)) {
            	excelRW.writeMultipleExcel(theForm, null, new Locale(lang, country), filePath + fileName);
			}
            
            File fileNew = new File(filePath + fileName);
    		byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
    		String encodedString = Base64.getEncoder().encodeToString(
    				fileContent);
    		String file1 = fileNew.getName();
    		response.setFileattachment(encodedString);
    		response.setFileType(fileType);
    		response.setFileName(file1);
            	
	          }   
	          catch(BTSLBaseException be) {
	 	    	 log.error(methodName, "Exception:e=" + be);
		         log.errorTrace(methodName, be);
		         if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
		             		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
		        	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		             responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED); 	 
		         } else{
		              response.setStatus(HttpStatus.SC_BAD_REQUEST);
		              responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		         }
		             String resmsg ="";
		             if(be.getArgs()!=null) {
		            	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());

		             }else {
		            	 resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);

		             }
		     	   response.setMessageCode(be.getMessage());
		     	   response.setMessage(resmsg);
		    	}catch (Exception e) {
	            log.error(methodName, "Exceptin:e=" + e);
	            log.errorTrace(methodName, e);
	            response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		    	}finally {
		    	try {
					if (mcomCon != null) {
						mcomCon.close("UserHierarchyController#"+methodName);
						mcomCon = null;
					}
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}

				try {
					if (con != null) {
						con.close();
					}
				} catch (Exception e) {
					log.errorTrace(methodName, e);
				}
		    	}
	          return response;
		}
		
		/**
		 * 
		 * @param channelUserVO
		 * @param userCategory
		 * @param status
		 * @return
		 * @throws BTSLBaseException
		 */
		public ArrayList<ChannelUserVO> loadUserList(ChannelUserVO channelUserVO,String userCategory,String status) throws BTSLBaseException {
	        final String METHOD_NAME = "loadUserList";
	        if (log.isDebugEnabled()) {
	            log.debug("loadUserList", "Entered");
	        }
	        ArrayList<ChannelUserVO> userList= null;
	        Connection con = null;MComConnectionI mcomCon = null;
	        ChannelUserTransferWebDAO channelUserTransferwebDAO = null;
	        
	        try {
	        	
	            String parentUserID=channelUserVO.getUserID();
	            if (BTSLUtil.isNullString(channelUserVO.getParentID())) {
	                parentUserID = channelUserVO.getOwnerID();
	            }
	            mcomCon = new MComConnection();con=mcomCon.getConnection();
	            channelUserTransferwebDAO = new ChannelUserTransferWebDAO();

	             userList = channelUserTransferwebDAO.loadChannelUserList(con, parentUserID, userCategory,null, PretupsI.ALL);
	             
	        } catch (Exception e) {
	            log.error("loadUserList", "Exceptin:e=" + e);
	            log.errorTrace(METHOD_NAME, e);
	           throw new BTSLBaseException(e);
	        } finally {
				if (mcomCon != null) {
					mcomCon.close("UserHierarchyAction#"+METHOD_NAME);
					
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug(METHOD_NAME, "Exiting");
	            }
	        }
	     return userList ;
	    }
		
		@GetMapping(value= "/loadProfileList",produces = MediaType.APPLICATION_JSON)
		@ResponseBody
		/*@ApiOperation( value = "Fetches User Grade List,Commission profile list, Transfer Profile List, Transfer Rule Type List and LMS Profile List ",response = ProfileListResponseVO.class,
				notes = ("Api Info:") + ("\n") + 
				 ("1. Enter category Code"),
				 authorizations = {
	    	            @Authorization(value = "Authorization")})
		@ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK", response = ProfileListResponseVO.class),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
		*/
		@io.swagger.v3.oas.annotations.Operation(summary = "${loadProfileList.summary}", description="${loadProfileList.description}",

				responses = {
						@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
								@io.swagger.v3.oas.annotations.media.Content(
										mediaType = "application/json",
										array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProfileListResponseVO.class))
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


		public ProfileListResponseVO loadProfileList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,HttpServletRequest httprequest,
				 @Parameter(description = "categoryCode", required = true) @RequestParam("categoryCode") String categoryCode,
				 HttpServletResponse responseSwag) {
			final String methodName = "loadProfileList";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			
			Connection con = null;
			MComConnectionI mcomCon = null;
			ProfileListResponseVO response = new ProfileListResponseVO();
			String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
			CategoryVO userCategoryVO = null;
		    UserDAO userDAO = null;
			CategoryVO categoryVO = null;
			ChannelUserDAO channelUserDao = null;
			ChannelUserWebDAO channelUserWebDao = null;
			TransferProfileDAO profileDAO = null;
			CategoryGradeDAO categoryGradeDAO = null;
			UserWebDAO  userWebDAO = null;
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				channelUserWebDao = new ChannelUserWebDAO();
		        profileDAO = new TransferProfileDAO();
		        categoryGradeDAO = new CategoryGradeDAO();
		        userWebDAO = new UserWebDAO();
		        
				OAuthUser OAuthUserData = new OAuthUser();
				OAuthUserData.setData(new OAuthUserData());
				OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
				String loginId = OAuthUserData.getData().getLoginid();
				UserVO userVO =new UserDAO().loadAllUserDetailsByLoginID(con, loginId);
				
		        
		        
				// load the User Grade dropdown
                List<GradeVO> gradeList=categoryGradeDAO.loadGradeList(con, categoryCode);
                response.setGradeList(gradeList);
                
                List<CommissionProfileSetVO> commissionProfileList = userWebDAO.loadCommisionProfileListByCategoryIDandGeography(con, categoryCode, userVO.getNetworkID(),null);
                response.setCommissionProfileList(commissionProfileList);

                List<ListValueVO> transferProfileList =profileDAO.loadTransferProfileByCategoryID(con, userVO.getNetworkID(), categoryCode,
                        PretupsI.PARENT_PROFILE_ID_USER);
                response.setTransferProfileList(transferProfileList);
            	
            	List<ListValueVO> transferRuleTypeList =LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true);
            	response.setTransferRuleTypeList(transferRuleTypeList);
            	
            	List<ListValueVO> lMSProfileList =channelUserWebDao.getLmsProfileList(con, userVO.getNetworkID());
            	response.setlMSProfileList(lMSProfileList);
				
				response.setStatus(200);
				
			}catch(BTSLBaseException be) {
				log.error("processRequest", "Exception:e=" + be);
				log.errorTrace(methodName, be);
				String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
				response.setMessageCode(be.getMessageKey());
				response.setMessage(msg);
				
				if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response.setStatus(401);
				}else {
					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(400);
				}
			}catch(Exception e) {
				log.error("processRequest", "Exception:e=" + e);
				log.errorTrace(methodName, e);
				
				response.setStatus(PretupsI.RESPONSE_FAIL);
	    		response.setMessageCode("error.general.processing");
	    		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			}finally {
				if(mcomCon != null) {
					mcomCon.close("ChannelUserServicesController#"+methodName);
					mcomCon=null;
				}
				if(log.isDebugEnabled()) {
					log.debug(methodName, "Exited");
				}
				try {
					if(con != null) {
						con.close();
					}
				}catch(Exception e) {
						log.errorTrace(methodName, e);
					}
				}
			
			return response;
		}

	@GetMapping(value ="/arearegionadmin", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	@io.swagger.v3.oas.annotations.Operation(summary = "${areasearchadmin.summary}", description="${areasearchadmin.description}",

			responses = {
					@ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = AreaSearchResponseVO.class))
							)
					}

					),


					@ApiResponse(responseCode = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_CODE, description = com.btsl.util.Constants.API_BAD_REQ_RESPONSE_DESC,  content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = com.btsl.common.BaseResponse.class))
							)
					}),
					@ApiResponse(responseCode = com.btsl.util.Constants.API_UNAUTH_RESPONSE_CODE, description = com.btsl.util.Constants.API_UNAUTH_RESPONSE_DESC,  content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = com.btsl.common.BaseResponse.class))
							)
					}),
					@ApiResponse(responseCode = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_CODE, description = com.btsl.util.Constants.API_NOT_FOUND_RESPONSE_DESC,  content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = com.btsl.common.BaseResponse.class))
							)
					}),
					@ApiResponse(responseCode = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_CODE, description = com.btsl.util.Constants.API_INTERNAL_ERROR_RESPONSE_DESC,  content = {
							@Content(
									mediaType = "application/json",
									array = @ArraySchema(schema = @Schema(implementation = com.btsl.common.BaseResponse.class))
							)
					})
			}
	)


	public AreaSearchResponseVO processRegionSearchAdmin(

			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httprequest,
			@Parameter(description = "geoDomainCode", required = true) @RequestParam("geoDomainCode") String geoDomainCode
	) {


		final String methodName = "processRegionSearchAdmin";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		AreaSearchResponseVO response = new AreaSearchResponseVO();
		String lang = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String country = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			String loginId = OAuthUserData.getData().getLoginid();
			response = channelUserServicesImpl.searchRegion(loginId, con, geoDomainCode, response1);
		}catch(BTSLBaseException be) {
			log.error("processAreaSearchAdmin", "Exception:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs());
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);

			if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(401);
			}else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(400);
			}
		}catch(Exception e) {
			log.error("processAreaSearchAdmin", "Exception:e=" + e);
			log.errorTrace(methodName, e);

			response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
		}finally {
			if(mcomCon != null) {
				mcomCon.close("ChannelUserServicesController#"+methodName);
				mcomCon=null;
			}
			if(log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
			try {
				if(con != null) {
					con.close();
				}
			}catch(Exception e) {
				log.errorTrace(methodName, e);
			}
		}

		return response;
	}

}



	






















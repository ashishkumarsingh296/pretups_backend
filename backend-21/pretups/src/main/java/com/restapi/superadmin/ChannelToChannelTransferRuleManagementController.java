package com.restapi.superadmin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
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
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.superadmin.requestVO.ChannelTransferRuleRequestVO;
import com.restapi.superadmin.responseVO.ChannelTransferRuleResponseVO;
import com.restapi.superadmin.responseVO.ChannelTransferRuleViewResponseVO;
import com.restapi.superadmin.serviceI.ChannelToChannelTransferRuleManagementServiceI;
import com.restapi.superadmin.util.ChannelTransferRuleCommonConstants;
import com.restapi.superadminVO.ChannelTransferRuleVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelToChannelTransferRuleManagementController.name}", description = "${ChannelToChannelTransferRuleManagementController.desc}")//@Api(tags = "Super Admin")
@RestController
@RequestMapping(value = "/v1/superadmin")
public class ChannelToChannelTransferRuleManagementController {
	public static final String TYPE = "CHANNEL";
	public static final String NETWORK_CODE = "NG";
	public static final String USER_CATEGORY = "CHANL";
	public static final Long LAST_MODIFY = 0L;
	public static final Log log = LogFactory.getLog(ChannelToChannelTransferRuleManagementController.class.getName());
	public static final String classname = "ChannelToChannelTransferRuleManagementController";

	@Autowired
	ChannelToChannelTransferRuleManagementServiceI channelToChannelTransferRuleManagementService;
	@Autowired
	static OperatorUtilI operatorUtili;

	@GetMapping(value = "/loadDomainList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Domain List", response = DomainListResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = DomainListResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadDomainList.summary}", description="${loadDomainList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DomainListResponseVO.class))
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


	public DomainListResponseVO getDomainList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

		final String methodName = "getDomainList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
			
		}
		DomainListResponseVO response = new DomainListResponseVO();

		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

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
			
			response = channelToChannelTransferRuleManagementService.viewDomainList(con);
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_DOMAIN_LIST_SUCCESS, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_DOMAIN_LIST_SUCCESS);
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
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					locale,
					PretupsErrorCodesI.LOAD_CHANNEL_TRANSFERRULE_LIST_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LOAD_CHANNEL_TRANSFERRULE_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		} 
		finally {

			if (mcomCon != null) {
				mcomCon.close("ChannelToChannelTransferRuleManagementController#getDomainList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:=" + methodName);
			}

		}
		return response;

	}
	

	@GetMapping(value = "/loadTransferRuleList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "TransferRule List", response = ChannelTransferRuleResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelTransferRuleResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${loadTransferRuleList.summary}", description="${loadTransferRuleList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelTransferRuleResponseVO.class))
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


	public ChannelTransferRuleResponseVO loadTransferRuleList(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "toDomainCode", required = true) @RequestParam("toDomainCode") String toDomainCode,
			HttpServletRequest httpServletRequest) throws Exception {
		final String METHOD_NAME = "loadTransferRuleList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelTransferRuleResponseVO channelTransferRuleResponseVO = new ChannelTransferRuleResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		String loginID = null;
		UserVO userVO = null;
		UserDAO userDAO = new UserDAO();
		ArrayList channelTransferRulesList = null;
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
					
			
			// loading the all transfer rules for the corresponds to the
			// selected category domain code
			channelTransferRulesList = channelToChannelTransferRuleManagementService.loadChannelTransferRuleVOList(con,
					userVO.getNetworkID(), domainCode, toDomainCode, TYPE);
			if (channelTransferRulesList == null || channelTransferRulesList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME,
						PretupsErrorCodesI.LOAD_CHANNEL_TRANSFERRULE_LIST_FAIL, 0, null);

			} else {

				channelTransferRuleResponseVO.setDomainName(domainCode);
				channelTransferRuleResponseVO.setToDomainName(toDomainCode);
				channelTransferRuleResponseVO.setTransferTypeList(channelTransferRulesList);
				channelTransferRuleResponseVO.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.LOAD_CHANNEL_TRANSFERRULE_LIST_SUCCESS, null);
				channelTransferRuleResponseVO.setMessage(resmsg);
				channelTransferRuleResponseVO.setMessageCode(PretupsErrorCodesI.LOAD_CHANNEL_TRANSFERRULE_LIST_SUCCESS);
			}
		} catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "BTSLBaseException:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			channelTransferRuleResponseVO.setMessageCode(e.getMessageKey());
			channelTransferRuleResponseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				channelTransferRuleResponseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				channelTransferRuleResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}

		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			channelTransferRuleResponseVO.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					locale,
					PretupsErrorCodesI.LOAD_CHANNEL_TRANSFERRULE_LIST_FAIL, null);
			channelTransferRuleResponseVO.setMessage(resmsg);
			channelTransferRuleResponseVO.setMessageCode(PretupsErrorCodesI.LOAD_CHANNEL_TRANSFERRULE_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelTransferRuleController#loadTransferRuleList");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting");
			}
		}

		return channelTransferRuleResponseVO;

	}
	
	@GetMapping(value = "/channelToChannelTransferruleDropdown", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Drop Down", response = ChannelTransferRuleResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ResponseEntity.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${channelToChannelTransferruleDropdown.summary}", description="${channelToChannelTransferruleDropdown.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelTransferRuleResponseVO.class))
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

	public ChannelTransferRuleResponseVO dropDown(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "toDomainCode", required = true) @RequestParam("toDomainCode") String toDomainCode,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {
		final String METHOD_NAME = "dropDown";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		

		ArrayList transferTypeList = null;
		ArrayList uncontrollTxnLevelList = null;
		ArrayList controllTxnLevelList = null;
		ArrayList fixedTxnLevelList = null;
		String loginID = null;
		// String userID = null;
		UserVO userVO = new UserVO();
		UserDAO userDAO = new UserDAO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ChannelTransferRuleResponseVO response = new ChannelTransferRuleResponseVO();
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

			userVO.getNetworkNamewithNetworkCode();

			DomainListResponseVO domainListResponse = channelToChannelTransferRuleManagementService.viewDomainList(con);
			
			
			response.setDomainName(domainListResponse.getDomainTypeList().stream()
					.filter(x -> x.getValue().equals(domainCode)).findFirst().get().getLabel());
			response.setToDomainName(domainListResponse.getDomainTypeList().stream()
					.filter(x -> x.getValue().equals(toDomainCode)).findFirst().get().getLabel());

			// loading the list of all products associated to the loging user
			// network.
			final ArrayList productList = channelToChannelTransferRuleManagementService.loadProductList(con,
					userVO.getNetworkID(), PretupsI.C2S_MODULE);
			if (productList == null || productList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LOAD_CHANNEL_PRODUCT_LIST_FAIL,
						0, null);
			} else {
				response.setProductList(productList);
			}
			
			/*
			 * loading the category list for the corresponds to the selected category domain
			 * code Here we get the arrayList of ListValueVO's which contains following
			 * values seperated by : in the values field sequence_no : category_code :
			 * hierarchy_allowed : uncntrl_transfer_allowed : restricted_msisdns
			 */
			final ArrayList categoryList = channelToChannelTransferRuleManagementService.loadCategoryList(con,
					domainCode);
			if (categoryList == null || categoryList.isEmpty()) {

				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LOAD_CHANNEL_CATEGORY_LIST_FAIL,
						0, null);
			}

			response.setCategoryList(categoryList);

			// now if from domain code is not same as to domain code then load
			// the to category list
			ArrayList toCategoryList = null;
			if (!domainCode.equals(toDomainCode)) {
				toCategoryList = channelToChannelTransferRuleManagementService.loadCategoryList(con, toDomainCode);
				if (toCategoryList == null || toCategoryList.isEmpty()) {
					throw new BTSLBaseException(classname, METHOD_NAME,
							PretupsErrorCodesI.LOAD_CHANNEL_CATEGORY_LIST_FAIL, 0, null);
				} else {
					response.setToCategoryList(toCategoryList);
					response.setAcrossDomain(true);
				}
			} else {
				response.setToCategoryList(categoryList);
				response.setAcrossDomain(false);
			}

			transferTypeList = LookupsCache.loadLookupDropDown(PretupsI.C2C_TRANSFER_TYPE, true);
			response.setTransferTypeList(transferTypeList);

			// if operation is performed on the C2C transfer rule then load some
			// other informations form the
			// lookup caches as Uncontroll level, controll level, fixed category
			// level etc, which are not
			// required in the case of O2C transfer rules.
			
			if (!USER_CATEGORY.equals(PretupsI.CATEGORY_TYPE_OPT)) {

				// generating the vo for not applicable condition
				final ListValueVO listValueVOForNA = new ListValueVO("Not applicable", "NA");
				// PretupsI.NOT_APPLICABLE);

				fixedTxnLevelList = LookupsCache.loadLookupDropDown(PretupsI.FIXED_LEVEL, true);
				if (fixedTxnLevelList != null) {
					fixedTxnLevelList.add(fixedTxnLevelList.size(), listValueVOForNA);
				}
				response.setFixedTransferLevelList(fixedTxnLevelList);

			
				uncontrollTxnLevelList = new ArrayList();
				controllTxnLevelList = new ArrayList();

				// if rule is with in the same domain then load
				// SELF,PARENT,OWNER and DOMAIN value
				if (!response.isAcrossDomain()) {
					uncontrollTxnLevelList
							.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
									PretupsI.UNCONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_SELF));
					uncontrollTxnLevelList
							.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
									PretupsI.UNCONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER));
					uncontrollTxnLevelList
							.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
									PretupsI.UNCONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN));

					controllTxnLevelList.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
							PretupsI.CONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_SELF));
					controllTxnLevelList.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
							PretupsI.CONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_OWNER));
					controllTxnLevelList.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
							PretupsI.CONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAIN));

					uncontrollTxnLevelList
							.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
									PretupsI.UNCONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT));
					controllTxnLevelList.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
							PretupsI.CONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_PARENT));

				} else {
					final ListValueVO fromDomainValueVO = BTSLUtil.getOptionDesc(domainCode,
							domainListResponse.getDomainTypeList());
					final ListValueVO toDomainValueVO = BTSLUtil.getOptionDesc(toDomainCode,
							domainListResponse.getDomainTypeList());
					// if rule is with in the same domainType then load
					// DOMAINTYPE value only
					if (fromDomainValueVO.getOtherInfo().equals(toDomainValueVO.getOtherInfo())) {
						uncontrollTxnLevelList
								.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
										PretupsI.UNCONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE));
						controllTxnLevelList
								.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
										PretupsI.CONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_DOMAINTYPE));
					}
					// if rule is not with in the same domainType then load
					// SYSTEM value only
					else {
						uncontrollTxnLevelList
								.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
										PretupsI.UNCONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM));
						controllTxnLevelList
								.add(channelToChannelTransferRuleManagementService.getListValueVOFromLookupsVO(
										PretupsI.CONTROLL_TXN_LEVEL, PretupsI.CHANNEL_TRANSFER_LEVEL_SYSTEM));
					}

				}
				uncontrollTxnLevelList.add(uncontrollTxnLevelList.size(), listValueVOForNA);
				controllTxnLevelList.add(controllTxnLevelList.size(), listValueVOForNA);

				
				response.setUncontrollTxnLevelList(uncontrollTxnLevelList);
				response.setControllTxnLevelList(controllTxnLevelList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);

				
			}
			
		} catch (BTSLBaseException e) {
			log.error("dropDown", "BTSLBaseException:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			response.setMessageCode(e.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					locale,
					PretupsErrorCodesI.CHANNEL_TRANSFERRULE_DROP_DOWN_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.CHANNEL_TRANSFERRULE_DROP_DOWN_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelToChannelTransferRuleManagementController#dropDown.");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "forward=" + METHOD_NAME);
			}
		}

		return response;
	}
	
	@PostMapping(value = "/addChannelTransferRule", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "addChannelTransferRule", response = ChannelTransferRuleResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelTransferRuleResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${addChannelTransferRule.summary}", description="${addChannelTransferRule.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelTransferRuleResponseVO.class))
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

	public ChannelTransferRuleResponseVO addChannelTransferRule(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody ChannelTransferRuleRequestVO reqChannelTransferRuleVO,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) {

		final String METHOD_NAME = "addChannelTransferRule";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		int addCount = 0;
		Date currentDate = null;

		String uniqueKeyEntered; // to store the entered values key
		String uniqueKeyExisting; // to store the existing values key

		UserVO userVO = null;
		UserDAO userDAO = new UserDAO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ChannelTransferRuleResponseVO response = new ChannelTransferRuleResponseVO();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			currentDate = new Date();

			int rejectCount = 0;
			String loginID = null;

			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

			ChannelTransferRuleVO channelTransferRuleVO = channelToChannelTransferRuleManagementService
					.requestVOToChangeDAOVO(reqChannelTransferRuleVO);
			channelTransferRuleVO.setNetworkCode(userVO.getNetworkID());
			boolean uniqueKeyExist = false;

			// check is transfer rule exist FROM CATEGORY to TO CATEGORY within
			// the same CATEGORY DOMAIN in the same NETWORK?
			if (channelTransferRuleVO != null) {

				uniqueKeyEntered = userVO.getNetworkID() + reqChannelTransferRuleVO.getDomainCode() + reqChannelTransferRuleVO.getFromCategory();

				uniqueKeyEntered += reqChannelTransferRuleVO.getToCategory();

				
				// checking that is record already exists
				ArrayList transferRuleList = channelToChannelTransferRuleManagementService
						.loadChannelTransferRuleVOList(con, userVO.getNetworkID(), reqChannelTransferRuleVO.getDomainCode(), reqChannelTransferRuleVO.getToDomainCode(), TYPE);
				for (int i = 0; i < transferRuleList.size(); i++) {

					ChannelTransferRuleVO existChannelTransferRuleVo = (ChannelTransferRuleVO) transferRuleList.get(i);

					uniqueKeyExisting = existChannelTransferRuleVo.getNetworkCode()
							+ existChannelTransferRuleVo.getDomainCode() + existChannelTransferRuleVo.getFromCategory()
							+ existChannelTransferRuleVo.getToCategory();
					if (uniqueKeyExisting.equals(uniqueKeyEntered)) {
						uniqueKeyExist = true;
						break;

					}
				}

					if (log.isDebugEnabled()) {
						log.debug(METHOD_NAME, "ischannelTransferRuleAlreadyExist=true");
					}
					if(uniqueKeyExist)
					throw new BTSLBaseException(this, METHOD_NAME,
							PretupsErrorCodesI.ADD_CHANNEL_TRANSFERRULE_ALREADY_EXIST, "addtrfrules");
				}
				// generating the unique key of the table chnl_transfer_rules.
				final String idType = PretupsI.CHANNEL_TRANSFER_RULE_ID;
				final StringBuffer uniqueTransferRuleID = new StringBuffer();
				final long transferRuleID = IDGenerator.getNextID(idType, TypesI.ALL);
				final int zeroes = 10 - (idType.length() + Long.toString(transferRuleID).length());
				for (int count = 0; count < zeroes; count++) {
					uniqueTransferRuleID.append(0);
				}
				uniqueTransferRuleID.insert(0, idType);
				uniqueTransferRuleID.append(Long.toString(transferRuleID));

				// setting the form category and to category to the VO here to
				// avoide data lost on the click on the back button.

				channelTransferRuleVO.setFromCategory(channelTransferRuleVO.getFromCategory());

				channelTransferRuleVO.setToCategory(channelTransferRuleVO.getToCategory());

			//Commenting for PRETUPS-24396
//				channelTransferRuleVO.setParentAssocationAllowed(PretupsI.NO);


			channelTransferRuleVO.setTransferRuleID(uniqueTransferRuleID.toString());
				channelTransferRuleVO.setCreatedOn(currentDate);
				channelTransferRuleVO.setModifiedOn(currentDate);
				channelTransferRuleVO.setCreatedBy(userVO.getUserID());
				channelTransferRuleVO.setModifiedBy(userVO.getUserID());
				channelTransferRuleVO.setLastModifiedTime(LAST_MODIFY);
				channelTransferRuleVO.setStatus(PretupsI.YES);
				

				addCount = channelToChannelTransferRuleManagementService.addChannelTransferRule(con,
						channelTransferRuleVO);
				if (con != null) {
					if (addCount > 0) {

						mcomCon.finalCommit();
						// log the data in adminOperationLog.log
						final AdminOperationVO adminOperationVO = new AdminOperationVO();
						adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
						adminOperationVO.setDate(currentDate);
						adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
						adminOperationVO.setInfo("Transfer rule (" + channelTransferRuleVO.getTransferRuleID()
								+ ") has added successfully between category " + channelTransferRuleVO.getFromCategory()
								+ " and " + channelTransferRuleVO.getToCategory());
						adminOperationVO.setLoginID(userVO.getLoginID());
						adminOperationVO.setUserID(userVO.getUserID());
						adminOperationVO.setCategoryCode(userVO.getCategoryCode());
						adminOperationVO.setNetworkCode(userVO.getNetworkID());
						adminOperationVO.setMsisdn(userVO.getMsisdn());
						AdminOperationLog.log(adminOperationVO);

						response.setStatus((HttpStatus.SC_OK));
						String resmsg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.ADD_CHANNEL_TRANSFERRULE_SUCCESSFULLY, null);
						response.setMessage(resmsg);
						response.setMessageCode(PretupsErrorCodesI.ADD_CHANNEL_TRANSFERRULE_SUCCESSFULLY);

					} else {

						mcomCon.finalRollback();
						throw new BTSLBaseException(this, "addChannelTransferRule", PretupsErrorCodesI.ADD_CHANNEL_TRANSFERRULE_FAIL
								, "displaytrfrule");
					}
				}
			
		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(METHOD_NAME, be);
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
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					locale,
					PretupsErrorCodesI.ADD_CHANNEL_TRANSFERRULE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ADD_CHANNEL_TRANSFERRULE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);

		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelToChannelTransferRuleManagementController#addChannelTransferRule");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("addChannelTransferRule", "Exiting return=" + addCount + ",forward=" + METHOD_NAME);
			}
		}
		return response;
	}
	
	@GetMapping(value = "/channelTransferRuleView", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Channel TransferRule View", response = ChannelTransferRuleResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelTransferRuleResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${channelTransferRuleView.summary}", description="${channelTransferRuleView.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelTransferRuleViewResponseVO.class))
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


	public ChannelTransferRuleViewResponseVO channelTransferRuleView(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "toDomainCode", required = true) @RequestParam("toDomainCode") String toDomainCode,
			@Parameter(description = "fromCategory", required = true) @RequestParam("fromCategory") String fromCategory,
			@Parameter(description = "toCategory", required = true) @RequestParam("toCategory") String toCategory,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {
		final String METHOD_NAME = "channelTransferRuleView";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered");
		}
		ChannelTransferRuleViewResponseVO response = new ChannelTransferRuleViewResponseVO();

		Connection con = null;
		MComConnectionI mcomCon = null;
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		
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
			
			
			ArrayList channelTransferRuleVOList = channelToChannelTransferRuleManagementService
					.loadChannelTransferRuleVOList(con, ChannelTransferRuleCommonConstants.NETWORK_CODE, domainCode,
							toDomainCode, TYPE);
			;
			// get selected transfer rule because its information is to be
			// displayed for the modification purpose

			ChannelTransferRuleVO channelTransferRuleVO = null;
			if (channelTransferRuleVOList != null && !channelTransferRuleVOList.isEmpty()) {

				for (int i = 0; i < channelTransferRuleVOList.size(); i++) {
					ChannelTransferRuleVO chnltrsRule = (ChannelTransferRuleVO) channelTransferRuleVOList.get(i);
					if (chnltrsRule.getDomainCode().equals(domainCode)
							&& chnltrsRule.getFromCategory().equals(fromCategory)
							&& chnltrsRule.getToCategory().equals(toCategory)
							&& chnltrsRule.getToDomainCode().equals(toDomainCode)) {
						channelTransferRuleVO = chnltrsRule;
						break;
					}
				}
				if (channelTransferRuleVO == null) {
					throw new BTSLBaseException(classname, METHOD_NAME,
							PretupsErrorCodesI.VIEW_CHANNEL_TRANSFERRULE_FAIL, 0, null);
				}
				channelToChannelTransferRuleManagementService
						.responseVOToChangerequestVO(response, channelTransferRuleVO);

                // get selected transfer rule because its information is to be
                // displayed for the modification purpose
               
                    if (channelTransferRuleVO.getProductVOList() != null && !channelTransferRuleVO.getProductVOList().isEmpty()) {
                        ListValueVO listValueVO;

                        // getting the products associated with the requested
                        // transfer rule.
                        final String[] productArray = new String[channelTransferRuleVO.getProductVOList().size()];
                        for (int i = 0, j = channelTransferRuleVO.getProductVOList().size(); i < j; i++) {
                            listValueVO = (ListValueVO) channelTransferRuleVO.getProductVOList().get(i);
                            productArray[i] = listValueVO.getValue();
                        }
                        channelTransferRuleVO.setProductArray(productArray);
                    }
                DomainListResponseVO domainResponse =channelToChannelTransferRuleManagementService.viewDomainList(con);
                ArrayList domainList = domainResponse.getDomainTypeList();
                for(int i = 0; i<domainList.size();i++) {
                	ListValueVO listValueVO = (ListValueVO)domainList.get(i);
                	if(listValueVO.getValue().equals(domainCode)) {
                		response.setDomainCodeDesc(listValueVO.getLabel());
                	}
                	if(listValueVO.getValue().equals(toDomainCode)) {
                		response.setToDomainCodeDesc(listValueVO.getLabel());
                	}
                }
                
                
                // else it will never true
                
                if (TYPE.equals(PretupsI.TRANSFER_RULE_TYPE_CHANNEL)) {
                    
                    ListValueVO listValueVO;
                    String catArr[] = null;
                    String catArr2[] = null;
                    
                    // now get the information attached with the category code
                    // for values of the other parametes
                    // as disable Uncontroll Transfer Allow
                    // disable Channel by pass Transfer etc.
                    // here catArr contains array of values as
                    // catArr={sequence_no,category_code,hierarchy_allowed,uncntrl_transfer_allowed,restricted_msisdns}
                    
                   ArrayList fromCategoryList = channelToChannelTransferRuleManagementService.loadCategoryList(con, domainCode);
                    for (int i = 0, j = fromCategoryList.size(); i < j; i++) {
                        listValueVO = (ListValueVO) fromCategoryList.get(i);
                        catArr = listValueVO.getValue().split("\\:");
                        if (catArr[1].equals(fromCategory)) {
                            fromCategory = listValueVO.getValue();
                            response.setFromCategoryDesc(listValueVO.getLabel());
                            
                            break;
                        }
                    }
                   
                    
                    
                    // now get the information attached with the to category
                    // code for values of the other parametes
                    // here catArr2 contains array of values as
                    // catArr2={sequence_no,category_code,hierarchy_allowed,uncntrl_transfer_allowed,restricted_msisdns}
                    
                    ArrayList toCategoryList = channelToChannelTransferRuleManagementService.loadCategoryList(con, toDomainCode);
                    
                    for (int i = 0, j = toCategoryList.size(); i < j; i++) {
                        listValueVO = (ListValueVO)toCategoryList.get(i);
                        catArr2 = listValueVO.getValue().split("\\:");
                        if (catArr2[1].equals(toCategory)) {
                            toCategory = listValueVO.getValue();
                            response.setToCategoryDesc(listValueVO.getLabel());
                            break;
                        }
                    }
                    
                    
                    response.setUncntrlTransferAllowedFlag(true);// used
                    // to disable and enable ParentAssociationAllowed checkbox
                    response.setParentAssociationAllowedFlag(false);// used
                    // to   disable and  enable the ChnlByPass  radiobutton
                    
                    response.setChnlByPassFlag(false);// used to
                    // disable  and enable RestrictedMsisdnAccess radiobutton
                    
                    response.setRestrictedMsisdnAccessFlag(true);// used
                    // to disable and enable the  restricted Recharge checkbox
                    
                    response.setRestrictedRechargeFlag(true);// used
                    
                    response.setToCategorySeqNumber(Integer.parseInt(catArr2[0])) ;// used
                    // to disable and enable the of fixed  categories
                    
                    response.setFromCategorySeqNumber(Integer.parseInt(catArr[0]));// used
                    
                    if (PretupsI.YES.equals(catArr2[4])) {
                    	// validation
                    	response.setRestrictedMsisdnAccessFlag(false);// used
                        // to disable  and enable RestrictedRecharge checkbox
                    	
                        response.setRestrictedRechargeFlag(false);// used
                        
                    }
                    if (PretupsI.YES.equals(catArr[3]) && PretupsI.YES.equals(catArr2[3])) {
                    	response.setUncntrlTransferAllowedFlag(false);// used
                    }
                    
                    
                    
                    
                    if (fromCategory.equals(toCategory) ) {
                    	response.setParentAssociationAllowedFlag(true);// used
                        // to disable and enable  the  radiobutton
                    	
                       response.setChnlByPassFlag(true);// used
                        
                    	response.setRestrictedMsisdnAccessFlag(true);// used
                        
                    	response.setRestrictedRechargeFlag(true);// used
                        
                    } else if (Integer.parseInt(catArr[0]) > Integer.parseInt(catArr2[0])) {
                    	response.setParentAssociationAllowedFlag(true);// used
                        // to  disable  and enable  the  radiobutton
                    	
                        response.setRestrictedMsisdnAccessFlag(true);// used
                        
                    	response.setRestrictedRechargeFlag(true);// used
                        
                    } else {
                        if (PretupsI.NO.equals(catArr[2])) {
                        	response.setParentAssociationAllowedFlag(true);// used
                                                   }
                    }
                }
            
                ChannelTransferRuleResponseVO dropDownResponse = this.dropDown(headers, domainCode, toDomainCode, response1, httpServletRequest);
				ArrayList transferTypeList = dropDownResponse.getTransferTypeList();
				for(int i=0; i<transferTypeList.size(); i++) {
					ListValueVO listValueVO = (ListValueVO)transferTypeList.get(i);
					if(response.getTransferType().equals(listValueVO.getValue())) {
						response.setTransferTypeDesc(listValueVO.getLabel());
					}
				}
				ArrayList controlTransferLevelList = dropDownResponse.getControllTxnLevelList();
				for(int i=0; i<controlTransferLevelList.size(); i++) {
					ListValueVO listValueVO = (ListValueVO)controlTransferLevelList.get(i);
					if(response.getCntrlTransferLevel().equals(listValueVO.getValue())) {
						response.setCntrlTransferLevelDesc(listValueVO.getLabel());
					}
					if(response.getCntrlWithdrawLevel().equals(listValueVO.getValue())) {
						response.setCntrlWithdrawLevelDesc(listValueVO.getLabel());
					}
					if(response.getCntrlReturnLevel().equals(listValueVO.getValue())) {
						response.setCntrlReturnLevelDesc(listValueVO.getLabel());
					}
				}
				ArrayList fixedTransferLevelList = dropDownResponse.getFixedTransferLevelList();
				for(int i=0; i<fixedTransferLevelList.size(); i++) {
					ListValueVO listValueVO = (ListValueVO)fixedTransferLevelList.get(i);
					if(response.getFixedTransferLevel().equals(listValueVO.getValue())) {
						response.setFixedTransferLevelDesc(listValueVO.getLabel());
					}
					if(response.getFixedWithdrawLevel().equals(listValueVO.getValue())) {
						response.setFixedWithdrawLevelDesc(listValueVO.getLabel());
					}
					if(response.getFixedReturnLevel().equals(listValueVO.getValue())) {
						response.setFixedReturnLevelDesc(listValueVO.getLabel());
					}

				}
				ArrayList uncontrolTransferLevelList= dropDownResponse.getUncontrollTxnLevelList();
				for(int i=0; i<uncontrolTransferLevelList.size(); i++) {
					ListValueVO listValueVO = (ListValueVO)uncontrolTransferLevelList.get(i);
					if(response.getUncntrlTransferLevel().equals(listValueVO.getValue())) {
						response.setUncntrlTransferLevelDesc(listValueVO.getLabel());
					}
					if(response.getUncntrlWithdrawLevel().equals(listValueVO.getValue())) {
						response.setUncntrlWithdrawLevelDesc(listValueVO.getLabel());
					}
					if(response.getUncntrlReturnLevel().equals(listValueVO.getValue())) {
						response.setUncntrlReturnLevelDesc(listValueVO.getLabel());
					}
				}
				
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.VIEW_CHANNEL_TRANSFERRULE_SUCCESS, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.VIEW_CHANNEL_TRANSFERRULE_SUCCESS);

			}

		} catch (BTSLBaseException be) {
			log.error("", "Exceptin:e=" + be);
			log.errorTrace(METHOD_NAME, be);
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
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					locale,
					PretupsErrorCodesI.VIEW_CHANNEL_TRANSFERRULE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VIEW_CHANNEL_TRANSFERRULE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		} finally {

			if (mcomCon != null) {
				mcomCon.close(classname + "#" + METHOD_NAME);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}

		}

		return response;

	}
	
	
	@PostMapping(value = "/updateChannelTransferRule", produces = MediaType.APPLICATION_JSON)
	/*@ApiOperation(value = "Update Channel TransferRule", response = ChannelTransferRuleResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelTransferRuleResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${updateChannelTransferRule.summary}", description="${updateChannelTransferRule.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelTransferRuleResponseVO.class))
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


	public ChannelTransferRuleResponseVO updateChannelTransferRule(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody ChannelTransferRuleRequestVO reqchannelTransferRuleVO, HttpServletResponse response1,
			HttpServletRequest httpServletRequest) {
		final String METHOD_NAME = "updateChannelTransferRule";
		if (log.isDebugEnabled()) {
			log.debug("updateChannelTransferRule", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		int updateCount = 0;

		String loginID = null;
		// String userID = null;
		UserVO userVO = new UserVO();
		UserDAO userDAO = new UserDAO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		Date currentDate = new Date();
		ChannelTransferRuleResponseVO response = new ChannelTransferRuleResponseVO();
		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

			ChannelTransferRuleVO updateChannelTransferRuleVO = channelToChannelTransferRuleManagementService
					.requestVOToChangeDAOVO(reqchannelTransferRuleVO);
			updateChannelTransferRuleVO.setNetworkCode(userVO.getNetworkID());
			updateChannelTransferRuleVO.setModifiedOn(currentDate);
			updateChannelTransferRuleVO.setModifiedBy(userVO.getUserID());
			updateChannelTransferRuleVO.setStatus(PretupsI.CHNL_TRANSFER_RULE_STATUS_MODIFY_REQUEST);
			updateChannelTransferRuleVO.setPreviousStatus(PretupsI.CHNL_TRANSFER_RULE_STATUS_ACTIVE);
			String uniqueKeyExisting;
			String uniqueKeyEntered = userVO.getNetworkID()
					+ updateChannelTransferRuleVO.getDomainCode() + updateChannelTransferRuleVO.getFromCategory()
					+ updateChannelTransferRuleVO.getToCategory();
			String transferId;

			// checking that is record already exists
			ArrayList transferRuleList = channelToChannelTransferRuleManagementService.loadChannelTransferRuleVOList(
					con, userVO.getNetworkID(), updateChannelTransferRuleVO.getDomainCode(),
					updateChannelTransferRuleVO.getToDomainCode(), ChannelTransferRuleCommonConstants.TYPE);
			for (int i = 0; i < transferRuleList.size(); i++) {

				ChannelTransferRuleVO existChannelTransferRuleVo = (ChannelTransferRuleVO) transferRuleList.get(i);

				uniqueKeyExisting = existChannelTransferRuleVo.getNetworkCode()
						+ existChannelTransferRuleVo.getDomainCode() + existChannelTransferRuleVo.getFromCategory()
						+ existChannelTransferRuleVo.getToCategory();

				if (uniqueKeyExisting.equals(uniqueKeyEntered)) {
					updateChannelTransferRuleVO.setTransferRuleID(existChannelTransferRuleVo.getTransferRuleID());
					break;

				}
			}
			
			updateCount = channelToChannelTransferRuleManagementService.updateChannelTransferRule(con,
					updateChannelTransferRuleVO);
			
			if (con != null) {
				if (updateCount > 0) {

					mcomCon.finalCommit();
					// log the data in adminOperationLog.log
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
					adminOperationVO.setInfo("Transfer rule (" + updateChannelTransferRuleVO.getTransferRuleID()
							+ ") has been updated successfully between category "
							+ updateChannelTransferRuleVO.getFromCategory() + " and "
							+ updateChannelTransferRuleVO.getToCategory());
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);

					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.UPDATE_CHANNEL_TRANSFERRULE_SUCCESSFULLY, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.UPDATE_CHANNEL_TRANSFERRULE_SUCCESSFULLY);

				} else {
					mcomCon.finalRollback();
					throw new BTSLBaseException(this, "updateChannelTransferRule",
							"channeltrfrule.updatetrfrule.msg.updateunsuccess", "displaytrfrule");
				}
			}
		} catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			response.setMessageCode(e.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					locale,
					PretupsErrorCodesI.UPDATE_CHANNEL_TRANSFERRULE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.UPDATE_CHANNEL_TRANSFERRULE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelToChannelTransferRuleManagementController#updateChannelTransferRule");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("updateChannelTransferRule", "Exiting return=" + updateCount);
			}
		}
		return response;

	}






	@PostMapping(value = "/deleteChannelTransferRule", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Delete Channel Transfer Rule", response = ChannelTransferRuleResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = ChannelTransferRuleResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${deleteChannelTransferRule.summary}", description="${deleteChannelTransferRule.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ChannelTransferRuleResponseVO.class))
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


	public ChannelTransferRuleResponseVO deleteChannelTransferRule(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "domainCode", required = true) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "toDomainCode", required = true) @RequestParam("toDomainCode") String toDomainCode,
			@Parameter(description = "fromCategory", required = true) @RequestParam("fromCategory") String fromCategory,
			@Parameter(description = "toCategory", required = true) @RequestParam("toCategory") String toCategory,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) {
		final String METHOD_NAME = "deleteChannelTransferRule";
		if (log.isDebugEnabled()) {
			log.debug("deleteChannelTransferRule", "Entered");
		}
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		Connection con = null;
		MComConnectionI mcomCon = null;
		Date currentDate = null;
		int deleteCount = 0;
		ChannelTransferRuleResponseVO response = new ChannelTransferRuleResponseVO();
		ChannelTransferRuleVO requestChannelTransferRuleVO = new ChannelTransferRuleVO();
		String loginID = null;
		String userID = null;
		UserVO userVO = new UserVO();
		UserDAO userDAO = new UserDAO();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			OAuthUser OAuthUserData = new OAuthUser();
			OAuthUserData.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);
			loginID = OAuthUserData.getData().getLoginid();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);

			currentDate = new Date();
			

			requestChannelTransferRuleVO.setModifiedOn(currentDate);
			requestChannelTransferRuleVO.setModifiedBy(userID);
			requestChannelTransferRuleVO.setLastModifiedTime(0l);
			requestChannelTransferRuleVO.setFromCategory(fromCategory);
			requestChannelTransferRuleVO.setToCategory(toCategory);
			requestChannelTransferRuleVO.setDomainCode(domainCode);
			requestChannelTransferRuleVO.setToDomainCode(toDomainCode);
			requestChannelTransferRuleVO.setNetworkCode(userVO.getNetworkID());
			requestChannelTransferRuleVO.setType(TYPE);
			
			// code changes

			deleteCount = channelToChannelTransferRuleManagementService.deleteChannelTransferRule(con,
					requestChannelTransferRuleVO);

			if (con != null) {
				if (deleteCount > 0) {

					mcomCon.finalCommit();
					// log the data in adminOperationLog.log
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
					adminOperationVO.setInfo("Transfer rule  has deleted successfully between category " + fromCategory
							+ " and " + toCategory);
					adminOperationVO.setLoginID(loginID);
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);

					
					response.setStatus((HttpStatus.SC_OK));
					String resmsg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.DELETE_CHANNEL_TRANSFERRULE_SUCCESS, null);
					response.setMessage(resmsg);
					response.setMessageCode(PretupsErrorCodesI.DELETE_CHANNEL_TRANSFERRULE_SUCCESS);
				} else {

					mcomCon.finalRollback();
					throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DELETE_CHANNEL_TRANSFERRULE_FAIL,
							"displaytrfrule");
				}
			}
		} catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "BTSLBaseException:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
			response.setMessageCode(e.getMessageKey());
			response.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					locale,
					PretupsErrorCodesI.DELETE_CHANNEL_TRANSFERRULE_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.DELETE_CHANNEL_TRANSFERRULE_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelToChannelTransferRuleManagementController#deleteChannelTransferRule");
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("deleteChannelTransferRule", "Exiting return=" + METHOD_NAME + " delete count" + deleteCount);
			}
		}

		return response;
	}
	private void log(AdminOperationVO p_adminOperationVO) {
		final Log log = LogFactory.getFactory().getInstance(AdminOperationLog.class.getName());
		final String METHOD_NAME = "log";
		StringBuffer strBuff = new StringBuffer();
		try {
			strBuff.append(" [Source :" + BTSLUtil.NullToString(p_adminOperationVO.getSource()) + "]");
			strBuff.append(" [Operation:" + BTSLUtil.NullToString(p_adminOperationVO.getOperation()) + "]");
			if (p_adminOperationVO.getDate() != null) {
				strBuff.append(
						" [Date & Time:" + BTSLDateUtil.getLocaleDateTimeFromDate(p_adminOperationVO.getDate()) + "]");
			} else {
				strBuff.append(" [Date & Time:" + BTSLDateUtil.getLocaleDateTimeFromDate(new Date()) + "]");
			}
			strBuff.append(" [Network Code:" + BTSLUtil.NullToString(p_adminOperationVO.getNetworkCode()) + "]");
			strBuff.append(" [Category Code:" + BTSLUtil.NullToString(p_adminOperationVO.getCategoryCode()) + "]");
			strBuff.append(" [Login ID:" + BTSLUtil.NullToString(p_adminOperationVO.getLoginID()) + "]");
			strBuff.append(" [User ID:" + BTSLUtil.NullToString(p_adminOperationVO.getUserID()) + "]");
			strBuff.append(" [Mobile No:" + BTSLUtil.NullToString(p_adminOperationVO.getMsisdn()) + "]");
			strBuff.append(" [Info:" + BTSLUtil.NullToString(p_adminOperationVO.getInfo()) + "]");

			log.info("", strBuff.toString());
		} catch (Exception e) {
			log.errorTrace(METHOD_NAME, e);
			log.error("log", p_adminOperationVO.getNetworkCode(), " Exception :" + e.getMessage());
		}
	}

}

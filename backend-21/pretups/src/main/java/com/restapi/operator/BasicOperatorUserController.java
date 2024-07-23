package com.restapi.operator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${BasicOperatorUserController.name}", description = "${BasicOperatorUserController.desc}")//@Api(tags = "Operator User")
@RestController
@RequestMapping(value = "/v1/optUser")
public class BasicOperatorUserController {

	public static final Log log = LogFactory.getLog(BasicOperatorUserController.class.getName());

	@GetMapping(value = "/roles", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags = "Operator User", value = "Get Roles ",

			authorizations = { @Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = RolesResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${roles.summary}", description="${roles.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RolesResponseVO.class))
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

	public RolesResponseVO getRoles(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "userCategeryCode", required = true) @RequestParam("userCategeryCode") String userCategeryCode,
			HttpServletResponse response) throws IOException, SQLException, BTSLBaseException {
		final String methodName = "getRolesAndServices";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}

		RolesResponseVO response1 = null;
		String messageArray[] = new String[1];
		Connection con = null;
		MComConnectionI mcomCon = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
		UserDAO userDAO = new UserDAO();
		VomsProductDAO voucherDAO = new VomsProductDAO();
		ArrayList voucherList = null;
		DomainDAO domainDAO = new DomainDAO();
		ArrayList domainList =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			response1 = new RolesResponseVO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response);
			} catch (Exception ex) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			if (BTSLUtil.isNullString(userCategeryCode)) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CATEGORY_NOT_EXIST,
						PretupsI.RESPONSE_FAIL, null);
			}
			ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());
			Map rolesMap = null;
			Map<String, HashMap<String, ArrayList<UserRolesVO>>> rolesMapNew = new LinkedHashMap<>();
			final UserRolesDAO userRolesDAO = new UserRolesDAO();
			rolesMap = userRolesDAO.loadRolesListByGroupRole_new_For_OptUser(con, userCategeryCode, "N");
			response1.setSystemRole(rolesMap);
			rolesMap = userRolesDAO.loadRolesListByGroupRole(con, userCategeryCode, "Y");
			response1.setGroupRole(rolesMap);
			response1.setService("ROLES");
			List<String> serviceTypeList = new ArrayList<String>();
			ArrayList<ListValueVO> serviceList = null;
			if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(userCategeryCode)
					|| PretupsI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userCategeryCode)) {
				serviceList = servicesDAO.assignServicesToChlAdmin(con, channelUserVO.getNetworkID());
			} else {
				serviceList = servicesDAO.loadServicesList(con, channelUserVO.getNetworkID());
			}

			if (serviceList != null) {
				int serviceLists = serviceList.size();
				ListValueVO listValueVO = null;
				for (int i = 0; i < serviceLists; i++) {
					listValueVO = serviceList.get(i);
					serviceTypeList.add(listValueVO.getCodeName());
				}
				response1.setServicesList(serviceTypeList);
			}
			voucherList = voucherDAO.loadVoucherTypeList(con);
			response1.setVoucherList(voucherList);
			domainList = domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE);
			response1.setDomainList(domainList);
			response1.setStatus(String.valueOf(HttpStatus.SC_OK));
			response1.setMessage("Roles list successfully fetched");
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response1.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			}
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),
							(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					be.getMessage(), messageArray);
			response1.setMessageCode(be.getMessage());
			response1.setMessage(resmsg);

		} catch (Exception e) {
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response1.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response1.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));

		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("BasicOperatorUserController#getRoles");
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		return response1;
	}

}

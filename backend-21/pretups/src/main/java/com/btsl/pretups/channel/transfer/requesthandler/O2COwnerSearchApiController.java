package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;


import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ErrorMap;
import com.btsl.common.ListValueVO;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${O2COwnerSearchApiController.name}", description = "${O2COwnerSearchApiController.desc}")//@Api(tags="O2C Services")
@RestController
@RequestMapping(value = "/v1/o2c")
public class O2COwnerSearchApiController {
	public static final Log log = LogFactory.getLog(O2COwnerSearchApiController.class.getName());

	/**
	 * @(#)O2COwnerSearchApiController.java This method gets the owner users
	 *                                            list
	 * 
	 * @param networkCode
	 * @param identifierType
	 * @param identifierValue
	 * @param idType
	 * @param id
	 * @param msisdn
	 * @param loginId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@Context
	private HttpServletRequest httpServletRequest;
	@GetMapping(value="/getOwnerUserDetails", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation( value = "OwnerUserList",response = SearchChannelUserVOResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK",response = SearchChannelUserVOResponseVO.class),
			@ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${getOwnerUserDetails.summary}", description="${getOwnerUserDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = SearchChannelUserVOResponseVO.class))
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




	public SearchChannelUserVOResponseVO getSearchOwnerUserDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = SwaggerAPIDescriptionI.USERNAME, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("userName") String userName,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_OWNER_CATEGORY, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("channelOwnerCategory") String channelOwnerCategory,
			@Parameter(description = SwaggerAPIDescriptionI.GEOGRAPHICAL_DOMAIN_CODE, example ="", required = true)
			@DefaultValue("superdistributer")@RequestParam("geoDomainCode") String geoDomainCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHANNEL_DOMAIN, example ="", required = true)
	        @DefaultValue("superdistributer")@RequestParam("DomainCode") String channelDomainCode)
			throws IOException, SQLException, BTSLBaseException {
		final String methodName = "getSearchOwnerUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		SearchChannelUserVOResponseVO response = null;
		 UserDAO userDao=null;
		 ArrayList<MasterErrorList> inputValidations=null;
		try {
			//basic form validation at api level
        	inputValidations = new ArrayList<>();
			response = new SearchChannelUserVOResponseVO();
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
			SearchUserRequestVO searchUserRequestVO=new SearchUserRequestVO();
			searchUserRequestVO.setData(new OAuthUserData());
			OAuthenticationUtil.validateTokenApi(searchUserRequestVO,headers);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();	
			UserVO userVO=new UserVO();
			userDao = new UserDAO();
			userVO=userDao.loadUsersDetails(con, searchUserRequestVO.getData().getMsisdn());
			
			ArrayList domList = userVO.getDomainList();
			Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
//			if(BTSLUtil.isNullString(userName))
//			{
//				MasterErrorList masterErrorList = new MasterErrorList();
//				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID,null);
//				 masterErrorList.setErrorCode(PretupsErrorCodesI.EXTSYS_REQ_USERNAME_BLANK_OR_LENGTH_INVALID);
//					masterErrorList.setErrorMsg(msg);
//					inputValidations.add(masterErrorList);
//				
//			}
			if(BTSLUtil.isNullString(geoDomainCode))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GEOGRAPHY_DOMAIN_CODE_DOES_NOT_EXIST,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.GEOGRAPHY_DOMAIN_CODE_DOES_NOT_EXIST);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
			}
			if(BTSLUtil.isNullString(channelOwnerCategory))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CHANNEL_OWNER_CATEGORY_NOT_EXIST,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.CHANNEL_OWNER_CATEGORY_NOT_EXIST);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
			}
			if(BTSLUtil.isNullString(channelDomainCode))
			{
				MasterErrorList masterErrorList = new MasterErrorList();
				 String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DOMAIN_INVALID,null);
				 masterErrorList.setErrorCode(PretupsErrorCodesI.DOMAIN_INVALID);
					masterErrorList.setErrorMsg(msg);
					inputValidations.add(masterErrorList);
			}
			if(!BTSLUtil.isNullOrEmptyList(inputValidations))
			{
				 response.setStatus("400");
				  response.setService("ownerUserDetailsResp");
				  response.setErrorMap(new ErrorMap());
				  response.getErrorMap().setMasterErrorList(inputValidations);
				  return response;
			}
			if ((domList == null || domList.isEmpty()) && PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO
					.getCategoryVO().getFixedDomains())) {
				domList = new DomainDAO().loadCategoryDomainList(con);
			}
			domList = BTSLUtil.displayDomainList(domList);
			ArrayList domList1 = new ArrayList<>();
			String channelDomain = null;
			if (domList.size() == 1) {
				ListValueVO listValueVO = null;
				listValueVO = (ListValueVO) domList.get(0);
				userVO.setDomainID(listValueVO.getValue());
				channelDomain = listValueVO.getValue();
				channelDomainCode=channelDomain;
			} else {
				domList1=domList;
			}
			boolean ischannelDomainCode =false;
			for(int i=0;i<domList1.size();i++)
			{
				if(((ListValueVO)domList1.get(i)).getValue().equals(channelDomainCode))
				{
					ischannelDomainCode =true;
					break;
				}
			}
			if(!ischannelDomainCode)
			{
				//validation
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DOMAIN_INVALID, 0, null,
						null);
			}
			final CategoryVO categoryVO = new CategoryWebDAO().loadOwnerCategory(con, channelDomainCode);
			/*
			if (categoryVO != null) {
				if(!categoryVO.getCategoryCode().equalsIgnoreCase(channelOwnerCategory))
				{
					//validation
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_OWNER_CATEGORY_NOT_EXIST, 0, null,
							null);
				}
			} */
			userName = "%" + userName + "%";
			userVO=userDao.loadUsersDetails(con, searchUserRequestVO.getData().getMsisdn());
			ArrayList list = new ChannelUserWebDAO().loadCategoryUsersWithinGeoDomainHirearchyMsisdn(con, channelOwnerCategory, userVO.getNetworkID(), userName, null, geoDomainCode, userVO.getUserID());
			/*
			 * Setting response messages
			 */
			if(BTSLUtil.isNullOrEmptyList(list))
			{
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OWNER_USER_LIST_DOES_NOT_EXIST, 0, null,
						null);
			}
			response.setOwnerUsersList(list);
			response.setService("ownerUserDetailsResp");
			response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			response.setService("ownerUserDetailsResp");
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					be.getArgs());
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			response.setMessageCode(e.toString());
			response.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("O2COwnerSearchApiController#" + "getSearchOwnerUserDetails");
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

			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, response);
			log.debug(methodName, "Exiting ");
		}

		return response;
	}
}

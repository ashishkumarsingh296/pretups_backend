package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;

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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${GetCategoryListC2C.name}", description = "${GetCategoryListC2C.desc}")//@Api(tags="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class GetCategoryListC2C {
	public static final Log log = LogFactory.getLog(GetCategoryListC2C.class.getName());

	/**
	 * @(#)GetCategoryListC2C.java This method gets the channel users
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
	@SuppressWarnings("unchecked")
	@GetMapping(value="/getCategoryListC2C", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@Produces(MediaType.APPLICATION_JSON)*/
	/*@ApiOperation( value = "CategoryList",response = CategoryListResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	        @ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK",response = CategoryListResponseVO.class),
			@ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${getCategoryListC2C.summary}", description="${getCategoryListC2C.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoryListResponseVO.class))
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



	public CategoryListResponseVO getCategoryListC2C(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
//			@ApiParam(  value = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true, allowableValues = "LOGINID,MSISDN") 
//			@RequestParam("identifierType") String identifiertype,
//			@ApiParam( value = SwaggerAPIDescriptionI.CHNL_USER_VALUE, example ="", required = true) 
//			@DefaultValue("superdistributer")@RequestParam("identifierValue") String identifiervalue,
			@Parameter(description = SwaggerAPIDescriptionI.CATEGORY_CODE, example ="")
			@RequestParam("categoryCode") String categoryCode ,
			@Parameter(description = SwaggerAPIDescriptionI.NETWORK_CODE, example ="", required = true)
			@RequestParam("networkCode") String networkCode,
			@Parameter(description = "transferType", required = true)//allowableValues = "T,W")
			@RequestParam("transferType") String transferType,
			HttpServletResponse response1)
			throws IOException, SQLException, BTSLBaseException {
		final String methodName = "getCategoryListC2C";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		CategoryListResponseVO response = null;
		 String messageArray[] = new String[1];
		 UserDAO userDao=null;
		 
		 OAuthUser oAuthUser= null;
		 OAuthUserData oAuthUserData =null;
		 String identifierValue = null;
			
		try {
			
			oAuthUser = new OAuthUser();
	    	oAuthUserData =new OAuthUserData();
	    	
	    	oAuthUser.setData(oAuthUserData);
	    	OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);    	
	    	
	    	identifierValue= oAuthUser.getData().getLoginid();
	    	
			response = new CategoryListResponseVO();
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
//			OAuthenticationUtil.validateTokenApi(headers);
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();	
			userDao = new UserDAO();
		/*
		 * Validating Network Code
		 */
			if (!BTSLUtil.isNullString(networkCode)) {
				NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
				if (networkVO == null) {
					messageArray[0] =  networkCode;
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID,
							messageArray);
				}
			} else {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0, null,
						null);
			}
             /*
              * Validation of parent user(loggedIn User)
              */
//			boolean validateuser = false;
//			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
//			validateuser = pretupsRestUtil.validateUser(identifiertype, identifiervalue, networkCode, con);
//			if (validateuser == false) {
//				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0, null, null);
//			}
			ChannelUserVO userVO=new ChannelUserVO();
			userVO= userDao.loadAllUserDetailsByLoginID(con, identifierValue);
			categoryCode=userVO.getCategoryCode();
//			if(BTSLUtil.isNullString(categoryCode))
//			{
//				if(identifiertype.equalsIgnoreCase("LOGINID"))
//					{
//					userVO= userDao.loadAllUserDetailsByLoginID(con, identifiervalue);
//					}
//			            else if(identifiertype.equalsIgnoreCase("MSISDN"))
//			            	{
//			            	userVO=userDao.loadUserDetailsByMsisdn(con, identifiervalue);
//			            	}
//				categoryCode=userVO.getCategoryCode();
//			}
			
			//mcomCon = new MComConnection();con=mcomCon.getConnection();
            final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
            final ArrayList catgList = channelTransferRuleWebDAO.loadTransferRulesCategoryList(con, networkCode, categoryCode);
            final ArrayList catgeoryList = new ArrayList();
            ChannelTransferRuleVO rulesVO = null;
            // Now filter the transfer rule list for which the Transfer allowed
            // field is 'Y' or Transfer Channel by pass is Y
            for (int i = 0, k = catgList.size(); i < k; i++) {
                rulesVO = (ChannelTransferRuleVO) catgList.get(i);
                if(transferType.equals("T")){
                if (PretupsI.YES.equals(rulesVO.getDirectTransferAllowed()) || PretupsI.YES.equals(rulesVO.getTransferChnlBypassAllowed())) {
                    // attached the domain code for the display purpose
                    catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes() , rulesVO.getToCategory()));
                }
                }
                else if(transferType.equals("W")){
                	 if (PretupsI.YES.equals(rulesVO.getWithdrawAllowed()) || PretupsI.YES.equals(rulesVO.getWithdrawChnlBypassAllowed())) {
                         // attached the domain code for the display purpose
                         catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes() , rulesVO.getToCategory()));
                     }	
                }
            }
            if(BTSLUtil.isNullOrEmptyList(catgeoryList))
            {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CATEGORYLIST);
            }
            response.setCategoryList(catgeoryList);

			/*
			 * Setting response messages
			 */
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
					be.getArgs());
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);

		} catch (Exception e) {
			log.error(methodName, "Exceptin:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.toString());
			response.setMessage(e.toString() + " : " + e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("FetchChannelUserDetailsController#" + "fetchChannelUserDetails");
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

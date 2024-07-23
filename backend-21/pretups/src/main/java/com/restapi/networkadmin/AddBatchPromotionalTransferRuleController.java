package com.restapi.networkadmin;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.restapi.networkadmin.requestVO.AddBatchPromotionalTransferRuleFileProcessingRequestVO;
import com.restapi.networkadmin.responseVO.DomainAndCategoryResponseVO;
import com.restapi.networkadmin.responseVO.DownloadFileResponseVO;
import com.restapi.networkadmin.responseVO.PromotinalLevelResponseVO;
import com.restapi.networkadmin.responseVO.UploadAndProcessFileResponseVO;
import com.restapi.networkadmin.serviceI.AddBatchPromotionalTransferRuleServiceI;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${AddBatchPromotionalTransferRuleController.name}", description = "${AddBatchPromotionalTransferRuleController.desc}")//@Api(tags = "Network Admin")
@RestController
@RequestMapping(value = "/v1/networkadmin")

public class AddBatchPromotionalTransferRuleController {

	
	
	@Autowired
	private AddBatchPromotionalTransferRuleServiceI addBatchPromotionalTransferRuleService;
		
	
	public static final Log LOG = LogFactory.getLog(AddBatchPromotionalTransferRuleController.class.getName());
	public static final String className = "AddBatchPromotionalTransferRuleController";

	
	@GetMapping(value = "/loadPromotionalLevelList", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load PromotionalLevel List", response =PromotinalLevelResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PromotinalLevelResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadPromotionalLevelList.summary}", description="${loadPromotionalLevelList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PromotinalLevelResponseVO.class))
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

	public PromotinalLevelResponseVO loadPromotionalLevelList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {
	    final String methodName = "loadPromotionalLevelList";
	    if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	    }
	    OAuthUser OAuthUserData = new OAuthUser();
		OAuthUserData.setData(new OAuthUserData());
		OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, response1);    
	    PromotinalLevelResponseVO promotinalLevelResponseVO = new PromotinalLevelResponseVO();
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		try { 
        ArrayList promotionalLevel = addBatchPromotionalTransferRuleService.loadPromotionalLevel();
        
        if (promotionalLevel == null || promotionalLevel.isEmpty()) {
            throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.LOAD_PROMOTIONAL_LEVEL_IS_EMPTY );
        }

        promotinalLevelResponseVO.setPromotionalLevelList(promotionalLevel);
        promotinalLevelResponseVO.setStatus((HttpStatus.SC_OK));
		String resmsg = RestAPIStringParser.getMessage(locale,
				PretupsErrorCodesI.LOAD_PROMOTIONAL_LEVEL_SUCCESSFULLY, null);
		promotinalLevelResponseVO.setMessage(resmsg);
		promotinalLevelResponseVO.setMessageCode(PretupsErrorCodesI.LOAD_PROMOTIONAL_LEVEL_SUCCESSFULLY);
		}
		catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
            promotinalLevelResponseVO.setMessageCode(e.getMessageKey());
            promotinalLevelResponseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				promotinalLevelResponseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				promotinalLevelResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				promotinalLevelResponseVO.setMessageCode(PretupsErrorCodesI.LOAD_PROMOTIONAL_LEVEL_FAIL);
			}
		}
         catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			promotinalLevelResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
			promotinalLevelResponseVO.setMessageCode(PretupsErrorCodesI.LOAD_PROMOTIONAL_LEVEL_FAIL);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting");
        }
        return promotinalLevelResponseVO;
        
		
		

	}
	@GetMapping(value = "/loadSearchCriteria", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load SearchCriteria", response =DomainAndCategoryResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = DomainAndCategoryResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadSearchCriteria.summary}", description="${loadSearchCriteria.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DomainAndCategoryResponseVO.class))
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

	public DomainAndCategoryResponseVO loadSearchCriteria(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, @Parameter(description = "promotionLevel", required = true) @RequestParam("promotionLevel") String promotionLevel,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {
		 final String methodName = "loadSearchCriteria";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	    DomainAndCategoryResponseVO domainAndCategoryResponseVO = new DomainAndCategoryResponseVO();  
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
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
			userDAO = new UserDAO();
		    String loginID = OAuthUserData.getData().getLoginid();
		    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			

		    domainAndCategoryResponseVO = addBatchPromotionalTransferRuleService.loadSearchCriteria(promotionLevel, con, userVO);
		    domainAndCategoryResponseVO.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.BATCH_POMOTIONAL_LELVEL_DROPDOWN_LOADED_SUCCESSFULLY, null);
			domainAndCategoryResponseVO.setMessage(resmsg);
			domainAndCategoryResponseVO.setMessageCode(PretupsErrorCodesI.BATCH_POMOTIONAL_LELVEL_DROPDOWN_LOADED_SUCCESSFULLY);
		   
        }catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
            domainAndCategoryResponseVO.setMessageCode(e.getMessageKey());
            domainAndCategoryResponseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				domainAndCategoryResponseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				domainAndCategoryResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				
			}
		}
         catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            domainAndCategoryResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            domainAndCategoryResponseVO.setMessageCode(PretupsErrorCodesI.BATCH_POMOTIONAL_LELVEL_DROPDOWN_LOAD_FAIL);
        }
       		
		 finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close(className+"#"+methodName);
        		mcomCon=null;
        		}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exit:forward=" + null);
            }
        }
        return domainAndCategoryResponseVO;
    }
	
	@GetMapping(value = "/loadDownLoadFile", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Load DownLoadFile", response =DownloadFileResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = DownloadFileResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${loadDownLoadFile.summary}", description="${loadDownLoadFile.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = DownloadFileResponseVO.class))
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

	public DownloadFileResponseVO loadDownLoadFile(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, @Parameter(description = "promotionLevel", required = true) @RequestParam("promotionLevel") String promotionLevel,
			@Parameter(description = "domainCode", required = false) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "categoryCode", required = false) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "geographicalCode", required = false) @RequestParam("geographicalCode") String geographicalCode,
			@Parameter(description = "cellGroupCode", required = false) @RequestParam("cellGroupCode") String cellGroupCode,
			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {

	    final String methodName = "loadDownLoadFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        DownloadFileResponseVO downloadFileResponseVO = new DownloadFileResponseVO();
        Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO userVO = null;
		UserDAO userDAO = null;
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
			userDAO = new UserDAO();
		    String loginID = OAuthUserData.getData().getLoginid();
		    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
		   
		    String 	selectType = PretupsI.DEFAULT_SELECT_TYPE ;
		   
		    downloadFileResponseVO = addBatchPromotionalTransferRuleService.loadDownloadFile(con, userVO, httpServletRequest, promotionLevel, domainCode, categoryCode, geographicalCode, cellGroupCode, selectType);
		    downloadFileResponseVO.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.LOAD_DOWNLOAD_FILE_SUCCESSFULLY, null);
			downloadFileResponseVO.setMessage(resmsg);
			downloadFileResponseVO.setMessageCode(PretupsErrorCodesI.LOAD_DOWNLOAD_FILE_SUCCESSFULLY);
		} catch (BTSLBaseException e) {
            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
            LOG.errorTrace(methodName, e);
            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
            downloadFileResponseVO.setMessageCode(e.getMessageKey());
            downloadFileResponseVO.setMessage(msg);

			if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				downloadFileResponseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				downloadFileResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
				
			}
		}
         catch (Exception e) {
            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
            LOG.errorTrace(methodName, e);
            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            downloadFileResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
            downloadFileResponseVO.setMessageCode(PretupsErrorCodesI.LOAD_DOWNLOAD_FILE_FAIL);
        }	finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close(className+"#"+methodName);
        		mcomCon=null;
        		}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exit:forward="+ methodName);
            }
        }
        return downloadFileResponseVO;
    }
	
	@PostMapping(value = "/uploadAndProcessFile", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "upload And ProcessFile", response =PromotinalLevelResponseVO.class, authorizations = {
			@Authorization(value = "Authorization") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK", response = PromotinalLevelResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })
*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${uploadAndProcessFile.summary}", description="${uploadAndProcessFile.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UploadAndProcessFileResponseVO.class))
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

	public UploadAndProcessFileResponseVO uploadAndProcessFile(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description = "promotionLevel", required = true) @RequestParam("promotionLevel") String promotionLevel,
			@Parameter(description = "domainCode", required = false) @RequestParam("domainCode") String domainCode,
			@Parameter(description = "categoryCode", required = false) @RequestParam("categoryCode") String categoryCode,
			@Parameter(description = "geographicalCode", required = false) @RequestParam("geographicalCode") String geographicalCode,
			@Parameter(description = "cellGroupCode", required = false) @RequestParam("cellGroupCode") String cellGroupCode,
			@RequestBody  AddBatchPromotionalTransferRuleFileProcessingRequestVO fileRequest,

			HttpServletResponse response1, HttpServletRequest httpServletRequest) throws Exception {
		 final String methodName = "uploadAndProcessFile";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	        
	        UploadAndProcessFileResponseVO uploadAndProcessFileResponseVO = new UploadAndProcessFileResponseVO();
	        Connection con = null;
			MComConnectionI mcomCon = null;
			UserVO userVO = null;
			UserDAO userDAO = null;
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
			
				userDAO = new UserDAO();
			    String loginID = OAuthUserData.getData().getLoginid();
			    userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			    
			    String selectType = PretupsI.DEFAULT_SELECT_TYPE ;
			    
			    uploadAndProcessFileResponseVO = addBatchPromotionalTransferRuleService.uploadAndProcessFile(con, httpServletRequest, response1, userVO, promotionLevel, domainCode, categoryCode, geographicalCode, cellGroupCode, selectType, fileRequest);
			    
			}catch (BTSLBaseException e) {
	            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
	            LOG.errorTrace(methodName, e);
	            String msg = RestAPIStringParser.getMessage(locale, e.getMessageKey(), null);
	            uploadAndProcessFileResponseVO.setMessageCode(e.getMessageKey());
	            uploadAndProcessFileResponseVO.setMessage(msg);

				if (Arrays.asList(PretupsI.OAUTHCODES).contains(e.getMessage())) {
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
					uploadAndProcessFileResponseVO.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
					uploadAndProcessFileResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
					
				}
			}
	         catch (Exception e) {
	            LOG.error(methodName, "Exceptin:e=" + e.getMessage());
	            LOG.errorTrace(methodName, e);
	            response1.setStatus(HttpStatus.SC_BAD_REQUEST);
	            uploadAndProcessFileResponseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
	            uploadAndProcessFileResponseVO.setMessageCode(PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL);
	        }	finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close(className+"#"+methodName);
	        		mcomCon=null;
	        		}
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(methodName, "Exit:forward="+ methodName);
	            }
	        }
	
	        return uploadAndProcessFileResponseVO;
	}
	

}
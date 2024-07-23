package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
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
import com.btsl.common.ErrorMap;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewResponse;
import com.btsl.pretups.channel.transfer.businesslogic.C2STotalTransactionCountRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoResponse;
import com.btsl.pretups.channel.transfer.businesslogic.TotalIncomeDetailsViewVO;
import com.btsl.pretups.channel.transfer.businesslogic.TransactionalDataRequest;
import com.btsl.pretups.channel.transfer.businesslogic.TransactionalDataResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalanceRequestVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.c2s.services.GetUserWidgetResponseVO;
import com.restapi.c2s.services.HomeScreenTransactionServiceI;
import com.restapi.user.service.C2STotalTransactionCountResponse;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.PendingTxnListResponseVO;
import com.restapi.user.service.TotalUserIncomeDetailViewResponse;
import com.restapi.user.service.UserBalanceResponseVO;
import com.restapi.user.service.UserHierachyRequestVO;
import com.restapi.user.service.UserHierarchyUIResponseData;
import com.restapi.user.service.UserHierarchyUIResponseVO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${HomeScreenTransactionController.name}", description = "${HomeScreenTransactionController.desc}")//@Api(tags ="C2S Receiver", value="C2S Receiver")
@RestController
@RequestMapping(value = "/v1/c2sReceiver")
public class HomeScreenTransactionController {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	public static final Log log = LogFactory.getLog(HomeScreenTransactionController.class.getName());
	StringBuilder loggerValue= new StringBuilder(); 
	
	@Autowired
	private HomeScreenTransactionServiceI homeScreenTransactionServiceI;
	
	@PostMapping(value= "/usrincview", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "Total Income detailed VIEW",
	           response = TotalUserIncomeDetailViewResponse.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = TotalUserIncomeDetailViewResponse.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${usrincview.summary}", description="${usrincview.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TotalUserIncomeDetailViewResponse.class))
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



	public TotalUserIncomeDetailViewResponse totalUserIncomeDetailsView(@RequestBody TotalIncomeDetailsViewVO totalIncomeDetailsViewVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "totalUserIncomeDetailsView";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		TotalUserIncomeDetailViewResponse response=null;
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
		
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        ChannelUserDAO channelUserDAO = null;
        Locale locale= new Locale(lang,country);
	    try {
	    	
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new TotalUserIncomeDetailViewResponse();
	    	new UserDAO();
	    	channelUserDAO = new ChannelUserDAO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);

	        String fromDate = totalIncomeDetailsViewVO.getData().getFromDate();
	        String toDate = totalIncomeDetailsViewVO.getData().getToDate();
	        String ExtNgCode = totalIncomeDetailsViewVO.getData().getExtnwcode();
	        
	        
	        DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        
	        try {
	        	patternDate.parse(fromDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "totalUserIncomeDetailsView",
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        try {
	        	patternDate.parse(toDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "totalUserIncomeDetailsView",
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        if(BTSLUtil.isNullString(ExtNgCode) || !ExtNgCode.equals(channelUserVO.getNetworkID())) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "totalUserIncomeDetailsView",
	        			PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
	        }

	        
			 homeScreenTransactionServiceI.getUserIncomeDetails(msisdn,totalIncomeDetailsViewVO,locale,response,responseSwag);
	        
		
	    }
	    catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,null);
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        
	        response.setMessageCode(msg);
	        response.setErrorMap(errorMap);
	        
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
	        response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#totalUserIncomeDetailsView");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("totalUserIncomeDetailsView", " Exited ");
	        }
	    }
	    
	    return response;
	}

	@PostMapping(value= "/c2sprodtxndetails", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "Channel User Service",
			notes=("Api Info:")+ ("\n") + (SwaggerAPIDescriptionI.C2S_PROD_TXN_DETAILS),
	           response = C2SAllTransactionDetailViewResponse.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = C2SAllTransactionDetailViewResponse.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2sprodtxndetails.summary}", description="${c2sprodtxndetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2SAllTransactionDetailViewResponse.class))
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



	public C2SAllTransactionDetailViewResponse c2SAllTransactionDetailView(@RequestBody C2SAllTransactionDetailViewRequestVO c2SAllTransactionDetailViewRequestVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "c2SAllTransactionDetailView";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    C2SAllTransactionDetailViewResponse response = null;
	    ChannelUserDAO channelUserDAO = null;
	    
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new C2SAllTransactionDetailViewResponse();
	    	channelUserDAO = new ChannelUserDAO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	
	    	
	        
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();
	    	ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);
	    	
	    	
	    	
	    	String fromDate = c2SAllTransactionDetailViewRequestVO.getData().getFromDate();
	        String toDate = c2SAllTransactionDetailViewRequestVO.getData().getToDate();
	        String ExtNgCode = c2SAllTransactionDetailViewRequestVO.getData().getExtnwcode();
	    	
	    	DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        
	        try {
	        	patternDate.parse(fromDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "totalUserIncomeDetailsView",
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        try {
	        	patternDate.parse(toDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "totalUserIncomeDetailsView",
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        if(BTSLUtil.isNullString(ExtNgCode) || !ExtNgCode.equals(channelUserVO.getNetworkID())) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "totalUserIncomeDetailsView",
	        			PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
	        }
	        
		    homeScreenTransactionServiceI.getC2SAllTransaction(msisdn,c2SAllTransactionDetailViewRequestVO,locale,response);

	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        
	        response.setMessageCode(msg);
	        response.setErrorMap(errorMap);
	        
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
	        response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#c2SAllTransactionDetailView");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("c2SAllTransactionDetailView", " Exited ");
	        }
	    }
	    
	    return response;
	}
	
	@PostMapping(value= "/c2stotaltrans", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "Total Transaction Count",
			notes=("Api Info:")+ ("\n") + (SwaggerAPIDescriptionI.C2S_PROD_TXN_DETAILS),
	           response = C2SAllTransactionDetailViewResponse.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = C2STotalTransactionCountResponse.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })
	*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2stotaltrans.summary}", description="${c2stotaltrans.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = C2STotalTransactionCountResponse.class))
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



	public C2STotalTransactionCountResponse c2sTotalTransactionCount(@RequestBody C2STotalTransactionCountRequestVO c2sTotalTransactionCountRequestVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "c2sTotalTransactionCount";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    ChannelUserDAO channelUserDAO = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    C2STotalTransactionCountResponse response = null;
	   
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
 
        Locale locale= new Locale(lang,country);
        
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new C2STotalTransactionCountResponse();
	    	channelUserDAO = new ChannelUserDAO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();
	    	
	    	
	    	
	    	String fromDate = c2sTotalTransactionCountRequestVO.getData().getFromDate();
	        String toDate = c2sTotalTransactionCountRequestVO.getData().getToDate();
	        String ExtNgCode = c2sTotalTransactionCountRequestVO.getData().getExtnwcode();
	    	
	    	DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	    	  try {
		        	patternDate.parse(fromDate);
		        }catch(Exception be) {
		        	throw new BTSLBaseException("HomeScreenTransactionController", "c2sTotalTransactionCount",
		        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
		        }
		        
		        try {
		        	patternDate.parse(toDate);
		        }catch(Exception be) {
		        	throw new BTSLBaseException("HomeScreenTransactionController", "c2sTotalTransactionCount",
		        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
		        }
		        ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);

		        if(BTSLUtil.isNullString(ExtNgCode) || !ExtNgCode.equals(channelUserVO.getNetworkID())) {
		        	throw new BTSLBaseException("HomeScreenTransactionController", "c2sTotalTransactionCount",
		        			PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
		        }
		        c2sTotalTransactionCountRequestVO.getData().setFromDate(BTSLDateUtil.getGregorianDateInString(c2sTotalTransactionCountRequestVO.getData().getFromDate()));
		        c2sTotalTransactionCountRequestVO.getData().setToDate(BTSLDateUtil.getGregorianDateInString(c2sTotalTransactionCountRequestVO.getData().getToDate()));
		        
			    homeScreenTransactionServiceI.getTotalTransactionCount(con,msisdn,c2sTotalTransactionCountRequestVO,locale,response);

		    } catch (BTSLBaseException be) {
		    	log.error(methodName, "Exception:e=" + be);
		        log.errorTrace(methodName, be);
		        
		        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
		        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
		        	response.setStatus((String.valueOf(HttpStatus.SC_UNAUTHORIZED)));
		        }
		        else {
		        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		        	response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
		        }
		        
		        masterError = new MasterErrorList();
				masterError.setErrorCode(be.getMessage());
		        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
				masterError.setErrorMsg(msg);
				masterErrorLists.add(masterError);
		        errorMap.setMasterErrorList(masterErrorLists);
		        
		        response.setMessage(msg);
		        response.setErrorMap(errorMap);
		       
		        
		    } catch(Exception e) {
		    	log.error(methodName, "Exceptin:e=" + e);
		        log.errorTrace(methodName, e);
		        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
		        response.setStatus(fail);
				response.setMessageCode("error.general.processing");
				response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
				
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
				
		    }  finally{
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#c2sTotalTransactionCount");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("c2sTotalTransactionCount", " Exited ");
	        }
	    }
	    
		return response;
	    }

	
	@PostMapping(value= "/pasbdet", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "Passbook View Info",
	           response = PassbookViewInfoResponse.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = PassbookViewInfoResponse.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${pasbdet.summary}", description="${pasbdet.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PassbookViewInfoResponse.class))
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



	public PassbookViewInfoResponse passbookView(@RequestBody PassbookViewInfoRequestVO passbookViewRequestVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "passbookView";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    PassbookViewInfoResponse response = null;
	    ChannelUserDAO channelUserDAO = null;
		String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new PassbookViewInfoResponse();
	    	channelUserDAO = new ChannelUserDAO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();
	    	ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);
	    	
	    	
	    	
	    	String fromDate = passbookViewRequestVO.getData().getFromDate();
	        String toDate = passbookViewRequestVO.getData().getToDate();
	        String ExtNgCode = passbookViewRequestVO.getData().getExtnwcode();
	    	
	    	DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        
	        try {
	        	patternDate.parse(fromDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "passbookView",
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        try {
	        	patternDate.parse(toDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "passbookView",
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        if(BTSLUtil.isNullString(ExtNgCode) || !ExtNgCode.equals(channelUserVO.getNetworkID())) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", "passbookView",
	        			PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
	        }
	        
		    homeScreenTransactionServiceI.getPassBookView(msisdn,passbookViewRequestVO,locale,response);
		
	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        
	        response.setMessageCode(msg);
	        response.setErrorMap(errorMap);
	        
	    }catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
	        response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#passbookView");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("passbookView", " Exited ");
	        }
	    }
	    
	    return response;
	}
	
	@PostMapping(value= "/o2cstockdetails", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "O2C Channel User Stock Details",
	           response = TransactionalDataResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = TransactionalDataResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cstockdetails.summary}", description="${o2cstockdetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TransactionalDataResponseVO.class))
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



	public TransactionalDataResponseVO o2CChannelUserStockDetailView(@RequestBody TransactionalDataRequest transactionalDataRequest,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "o2CChannelUserStockDetailView";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    TransactionalDataResponseVO response = null;
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new TransactionalDataResponseVO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	
	    	
	        
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();	    	
	    		    	
	    	String fromDate = transactionalDataRequest.getFromDate();
	        String toDate = transactionalDataRequest.getToDate();
	      
	    	
	    	DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        
	        try {
	        	patternDate.parse(fromDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        try {
	        	patternDate.parse(toDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        
		    homeScreenTransactionServiceI.getChannelUserTxnDetail(msisdn,fromDate,toDate,"O2C","T",locale,response);

	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        
	        response.setMessageCode(msg);
	      //  response.setErrorMap(errorMap);
	        
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#o2CChannelUserStockDetailView");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("o2CChannelUserStockDetailView", " Exited ");
	        }
	    }
	    
	    return response;
	}
	
	
	@PostMapping(value= "/c2cstockdetails", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "C2C Channel User Stock Details",
	           response = TransactionalDataResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = TransactionalDataResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2cstockdetails.summary}", description="${c2cstockdetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TransactionalDataResponseVO.class))
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




	public TransactionalDataResponseVO c2CChannelUserStockDetailView(@RequestBody TransactionalDataRequest transactionalDataRequest,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "c2CChannelUserStockDetailView";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    TransactionalDataResponseVO response = null;
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new TransactionalDataResponseVO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	
	    	
	        
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();	    	
	    		    	
	    	String fromDate = transactionalDataRequest.getFromDate();
	        String toDate = transactionalDataRequest.getToDate();
	      
	    	
	    	DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        
	        try {
	        	patternDate.parse(fromDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController",methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        try {
	        	patternDate.parse(toDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        
		    homeScreenTransactionServiceI.getChannelUserTxnDetail(msisdn,fromDate,toDate,"C2C","T",locale,response);

	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        
	        response.setMessageCode(msg);
	      //  response.setErrorMap(errorMap);
	        
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#c2CChannelUserStockDetailView");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("c2CChannelUserStockDetailView", " Exited ");
	        }
	    }
	    
	    return response;
	}
	
	@PostMapping(value= "/c2cvoucherdetails", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "C2C Channel User Voucher Details",
	           response = TransactionalDataResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = TransactionalDataResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2cvoucherdetails.summary}", description="${c2cvoucherdetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TransactionalDataResponseVO.class))
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



	public TransactionalDataResponseVO c2CChannelUserVoucherDetailView(@RequestBody TransactionalDataRequest transactionalDataRequest,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "c2CChannelUserVoucherDetailView";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    TransactionalDataResponseVO response = null;
	    
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new TransactionalDataResponseVO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	
	    	
	        
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();	    	
	    		    	
	    	String fromDate = transactionalDataRequest.getFromDate();
	        String toDate = transactionalDataRequest.getToDate();
	      
	    	
	    	DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        
	        try {
	        	patternDate.parse(fromDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        try {
	        	patternDate.parse(toDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        
		    homeScreenTransactionServiceI.getChannelUserTxnDetail(msisdn,fromDate,toDate,"C2C","V",locale,response);

	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        
	        response.setMessageCode(msg);
	      //  response.setErrorMap(errorMap);
	        
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#c2CChannelUserVoucherDetailView");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("c2CChannelUserVoucherDetailView", " Exited ");
	        }
	    }
	    
	    return response;
	}
	
	@PostMapping(value= "/o2cvoucherdetails", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "O2C Channel User Voucher Details",
	           response = TransactionalDataResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = TransactionalDataResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${o2cvoucherdetails.summary}", description="${o2cvoucherdetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TransactionalDataResponseVO.class))
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



	public TransactionalDataResponseVO o2CChannelUserVoucherDetailView(@RequestBody TransactionalDataRequest transactionalDataRequest,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "o2CChannelUserVoucherDetailView";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    TransactionalDataResponseVO response = null;
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new TransactionalDataResponseVO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	
	    	
	        
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();	    	
	    		    	
	    	String fromDate = transactionalDataRequest.getFromDate();
	        String toDate = transactionalDataRequest.getToDate();
	      
	    	
	    	DateTimeFormatter patternDate= DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        
	        try {
	        	patternDate.parse(fromDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        try {
	        	patternDate.parse(toDate);
	        }catch(Exception be) {
	        	throw new BTSLBaseException("HomeScreenTransactionController", methodName,
	        			PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
	        }
	        
	        
		    homeScreenTransactionServiceI.getChannelUserTxnDetail(msisdn,fromDate,toDate,"O2C","V",locale,response);

	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        
	        response.setMessageCode(msg);
	      //  response.setErrorMap(errorMap);
	        
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        response.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#o2CChannelUserVoucherDetailView");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug("o2CChannelUserVoucherDetailView", " Exited ");
	        }
	    }
	    
	    return response;
	}
	 
	
	@GetMapping(value= "/PendingTxnListCount", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "PendingTransactionListCount",
	           response = PendingTxnListResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = PendingTxnListResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${PendingTxnListCount.summary}", description="${PendingTxnListCount.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PendingTxnListResponseVO.class))
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



	public PendingTxnListResponseVO PendingTxnListCount(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				@Parameter(description = "Type", required = true)//allowableValues = "O2C,C2C")
				@RequestParam("Type") String type,
				HttpServletResponse responseSwag)throws BTSLBaseException {
		
			final String methodName =  "PendingTxnListCount";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser=null;
		MasterErrorList masterError=null;
	    ErrorMap errorMap = null;
	    ArrayList<MasterErrorList> masterErrorLists = null;
	    PendingTxnListResponseVO response = null;
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	errorMap = new ErrorMap();
	    	masterErrorLists = new ArrayList<>();
	    	response = new PendingTxnListResponseVO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	 
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();
	    	homeScreenTransactionServiceI.getPendingtxnList(msisdn, type,response,locale);
	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
	        }
	        
	        masterError = new MasterErrorList();
			masterError.setErrorCode(be.getMessage());
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
			masterError.setErrorMsg(msg);
			masterErrorLists.add(masterError);
	        errorMap.setMasterErrorList(masterErrorLists);
	        response.setErrorMap(errorMap);
	        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
	        response.setStatus(fail);
	        response.setMessageCode(fail);
	        responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        String fail=Integer.toString(PretupsI.RESPONSE_FAIL) ;
	        response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
	    }  
	    finally {
	        if (_log.isDebugEnabled()) {
	            _log.debug("PendingTxnListCount", " Exited ");
	        }
	    }
	    
	    return response;
	}
	
	@GetMapping(value= "/getUserWidget", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "GetUserAllowedWidget",
	           response = GetUserWidgetResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = GetUserWidgetResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${getUserWidget.summary}", description="${getUserWidget.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetUserWidgetResponseVO.class))
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



	public GetUserWidgetResponseVO getUserWidgetList(@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				HttpServletResponse responseSwag)throws BTSLBaseException {
		
			final String methodName =  "getUserWidgetList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser=null;
	    GetUserWidgetResponseVO response = null;
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale= new Locale(lang,country);
	    
	    try {
	    	
	    	response = new GetUserWidgetResponseVO();
	    	oAuthUser=new OAuthUser();
	    	oAuthUser.setData(new OAuthUserData());
	    	try {
	    		OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
	    	}catch(Exception ex) {
	    		throw new BTSLBaseException(
	    				HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
	    	}
	    	
	    	String msisdn = oAuthUser.getData().getMsisdn();
	    	ArrayList<String> widgetList = homeScreenTransactionServiceI.getUserWidgetList(msisdn);
	    	response.setWidgetList(widgetList);
	    	String[] arr = {};
	    	if(widgetList.size() >0) {
	    		response.setStatus(200);
	    		response.setMessageCode(PretupsErrorCodesI.TXN_SUCCES);
	    		String msg=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.TXN_SUCCES ,arr);
	 	        response.setMessage(msg);
	    	}else {
	    		response.setStatus(200);
	    		response.setMessageCode(PretupsErrorCodesI.NO_WIGET_ALLOTED);
	    		String msg=RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.NO_WIGET_ALLOTED ,arr);
	 	        response.setMessage(msg);
	    	}
	    } catch (BTSLBaseException be) {
	    	log.error(methodName, "Exception:e=" + be);
	        log.errorTrace(methodName, be);
	        
	        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
	        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
	        }
	        else {
	        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
	        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
	        }
	        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());
	        response.setMessageCode(msg);
	    } catch(Exception e) {
	    	log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        int fail=PretupsI.RESPONSE_FAIL ;
	        response.setStatus(fail);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
			
			responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
			
	    }  
	    finally {
	        if (_log.isDebugEnabled()) {
	            _log.debug("o2CChannelUserStockDetailView", " Exited ");
	        }
	    }
	    
	    return response;
	}
	
	
	@PostMapping(value= "/c2sUserBalance", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "User balance info",
			notes=("Api Info:")+ ("\n") + (SwaggerAPIDescriptionI.BALANCE_DETAILS),
	           response = UserBalanceResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = UserBalanceResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${c2sUserBalance.summary}", description="${c2sUserBalance.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserBalanceResponseVO.class))
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



	public UserBalanceResponseVO getUserBalance(@RequestBody UserBalanceRequestVO c2sTotalTransactionCountRequestVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "getUserBalance";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser=null;
	    Connection con = null;
	    ChannelUserDAO channelUserDAO = null;
	    MComConnectionI mcomCon = null;
	    UserBalanceResponseVO response = null;
	   
	    String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
 
        Locale locale= new Locale(lang,country);
        
		try {
			response = new UserBalanceResponseVO();
			channelUserDAO = new ChannelUserDAO();
			oAuthUser = new OAuthUser();
			oAuthUser.setData(new OAuthUserData());

			try {
				OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			} catch (Exception ex) {
				throw new BTSLBaseException(HomeScreenTransactionController.class.getName(), methodName,
						PretupsErrorCodesI.UNAUTHORIZED_REQUEST);
			}

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn = oAuthUser.getData().getMsisdn();

			String fromDate = c2sTotalTransactionCountRequestVO.getFromDate();
			String toDate = c2sTotalTransactionCountRequestVO.getToDate();

			DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			try {
				patternDate.parse(fromDate);
				patternDate.parse(toDate);
			} catch (Exception be) {
				throw new BTSLBaseException("HomeScreenTransactionController", methodName,
						PretupsErrorCodesI.EXTSYS_REQ_DATE_INVALID_FORMAT, 0, null);
			}
			SimpleDateFormat sdf = new SimpleDateFormat(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			Date currentDate = new Date();
			Date frDate =  sdf.parse(fromDate);
			Date tDate = sdf.parse(toDate);
			
			 if (frDate.getTime() > currentDate.getTime()) 
				{
					throw new BTSLBaseException("HomeScreenTransactionController", methodName,
							PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
				}
				if (tDate.getTime() > currentDate.getTime()) 
				{
					throw new BTSLBaseException("HomeScreenTransactionController", methodName,
							PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
				}
				if (frDate.getTime() > tDate.getTime()) 
				{
					throw new BTSLBaseException("HomeScreenTransactionController", methodName,
							PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
				}
			
			ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);

			homeScreenTransactionServiceI.getUserBalances(channelUserVO, c2sTotalTransactionCountRequestVO, locale, response);
			response.setMessage(PretupsI.SUCCESS);
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsI.GATEWAY_MESSAGE_SUCCESS);
			

		} catch (BTSLBaseException be) {
		    	log.error(methodName, "Exception:e=" + be);
		        log.errorTrace(methodName, be);
		        
		        if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
		        	responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
		        	response.setStatus(HttpStatus.SC_UNAUTHORIZED);
		        }
		        else {
		        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
		        }
		        

		        String msg=RestAPIStringParser.getMessage(locale,be.getMessage() ,be.getArgs());

		        
		        response.setMessage(msg);
		       
		        
		    } catch(Exception e) {
		    	log.error(methodName, "Exceptin:e=" + e);
		        log.errorTrace(methodName, e);
		        response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessageCode("error.general.processing");
				response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
				
				responseSwag.setStatus(PretupsI.RESPONSE_FAIL);
				
		    }  finally{
	    	if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#"+methodName);
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
	    }
	    
		return response;
	    }
	
	@PostMapping(value= "/UserHierarchyList", produces = MediaType.APPLICATION_JSON)	
	/*@ApiOperation(value = "User hierarchy info",
			notes=("Api Info:")+ ("\n") + (SwaggerAPIDescriptionI.BALANCE_DETAILS),
	           response = UserBalanceResponseVO.class,
	           authorizations = {
	               @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	      @ApiResponse(code = 200, message = "OK", response = UserBalanceResponseVO.class),
	      @ApiResponse(code = 400, message = "Bad Request" ),
	      @ApiResponse(code = 401, message = "Unauthorized"),
	      @ApiResponse(code = 404, message = "Not Found")
	      })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${UserHierarchyList.summary}", description="${UserHierarchyList.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserHierarchyUIResponseVO.class))
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



	public UserHierarchyUIResponseVO getUserHierarchy(@RequestBody UserHierachyRequestVO requestVO,
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 HttpServletResponse responseSwag) {
		
		final String methodName =  "getUserHierarchy";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		OAuthUser oAuthUser = new OAuthUser();
		String loginID = null;
		UserHierarchyUIResponseVO response = new UserHierarchyUIResponseVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
	    String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
	    Locale locale = new Locale(defaultLanguage, defaultCountry);
		oAuthUser.setData(new OAuthUserData());
		try
		{
			mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);

			loginID = oAuthUser.getData().getLoginid();
			
			UserHierarchyUIResponseData responseObj = new UserHierarchyUIResponseData();
			response.setUserHierarchyUIResponseData(responseObj);
			
			int maxLevel = homeScreenTransactionServiceI.getUserHierarchyList(con, loginID, requestVO, response.getUserHierarchyUIResponseData(), responseSwag);
			response.setStatus(HttpStatus.SC_OK);
			response.setLevel(maxLevel);
			response.setMessage(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.USER_HIERARCHY_SUCCESS_MESSAGE, null));
		}
		catch (BTSLBaseException be) 
		{
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			}
			else
			{
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				String resmsg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);
			}
			
		} 
		catch (Exception e) 
		{
			responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			String resmsg = RestAPIStringParser.getMessage(locale, e.getMessage(), null);
			response.setMessageCode(String.valueOf(HttpStatus.SC_BAD_REQUEST));
			response.setMessage("error.general.processing");
		}
		finally
		{
			if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController#getUserHierarchy");
				mcomCon = null;
			}
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, " Exited ");
	        }
		}
		return response;
		}
	
	@PostMapping(value= "/userHierarchyDownload", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "User hierarchy download for Channel User and Channel Admin",
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = FileDownloadResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${userHierarchyDownload.summary}", description="${userHierarchyDownload.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FileDownloadResponse.class))
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



	public FileDownloadResponse userHierarchyDownloadController(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody UserHierarchyDownloadRequestVO requestVO,
			HttpServletResponse response)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "userHierarchyDownloadController";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered"); 
        }
        UserVO userVO = null;
        UserDAO userDao = null;
        MComConnectionI mcomCon = null;
	    Connection con = null;
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        try{
	        
        	OAuthUser oAuthUserData=new OAuthUser();
	        oAuthUserData.setData(new OAuthUserData());
  
	            
	        OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response);
        	
        	OAuthenticationUtil.validateTokenApi(headers);
        	
        	mcomCon = new MComConnection();
        	con = mcomCon.getConnection();
        	userDao = new UserDAO();
        	String loginId =  oAuthUserData.getData().getLoginid();
            userVO = userDao.loadAllUserDetailsByLoginID(con, loginId);
        	
        	fileDownloadResponse = homeScreenTransactionServiceI.generateUserHierarchyFile(con, userVO, requestVO, response);
        } 
        catch (BTSLBaseException be) {
          	   log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(HttpStatus.SC_UNAUTHORIZED);
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(HttpStatus.SC_BAD_REQUEST);
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), be.getArgs());
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	   
   	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(PretupsI.RESPONSE_FAIL);
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        finally {

	    	try {
	        	if (mcomCon != null) {
					mcomCon.close("DownloadTemplateController#"+methodName);
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
        return fileDownloadResponse;
	}
	
}

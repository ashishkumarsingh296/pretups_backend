package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.receiver.RestReceiver;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteRequestParentVO;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsData;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.AutoCompleteUserDetailsResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/*@Path("")*/
@Tag(name = "C2C Receiver", description = "C2C Receiver")//@Api(tags= "C2C Receiver")
@RestController
@RequestMapping(value = "/v1/c2cReceiver")
public class AutoCompleteUserDetailsController implements ServiceKeywordControllerI {

	protected final Log _log = LogFactory.getLog(getClass().getName());
	ChannelUserVO channelUserVO = null;
	String loginID = "";
	String msisdn = "";


	private static final String PROCESS = "AutoCompleteUserDetailsController[process]";

 
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/autocomplete", consumes =MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	
	/*@ApiOperation(tags= "C2C Receiver", value = "Auto complete User Details", response = PretupsResponse.class,
			notes = SwaggerAPIDescriptionI.AUTOCMPLT_USER_DETAILS_DESCRIPTION,
					authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = PretupsResponse.class),
	        @ApiResponse(code = 201, message = "Created"),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${autocomplete.summary}", description="${autocomplete.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AutoCompleteResponseVO.class))
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



	public AutoCompleteResponseVO processCP2PUserRequest(HttpServletRequest httpServletRequest,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter( required =true)
			@RequestBody AutoCompleteRequestParentVO autoCompleteRequestParentVO, HttpServletResponse response1) 
	{

		final String methodName = "processCP2PUserRequest_AutoCompleteUserDetailsController";
		PretupsResponse<JsonNode> response = null;
		RestReceiver restReceiver;
		RestReceiver.updateRequestIdChannel();
		AutoCompleteResponseVO autoCompleteResponseVO = new AutoCompleteResponseVO();
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		final String requestIDStr = String.valueOf(RestReceiver.getRequestIdChannel());
		List<AutoCompleteUserDetailsResponseVO> responseObj = new ArrayList<AutoCompleteUserDetailsResponseVO>();

		restReceiver = new RestReceiver();
		

		try {
			/*
			 * Authentication
			 * 
			 * @throws BTSLBaseException
			 */
			oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers, response1);

			loginID = oAuthUser.getData().getLoginid();

			autoCompleteRequestParentVO.setServicePort(oAuthUser.getServicePort());
			autoCompleteRequestParentVO.setReqGatewayCode(oAuthUser.getReqGatewayCode());
			autoCompleteRequestParentVO.setReqGatewayLoginId(oAuthUser.getReqGatewayLoginId());
			autoCompleteRequestParentVO.setReqGatewayPassword(oAuthUser.getReqGatewayPassword());
			autoCompleteRequestParentVO.setReqGatewayType(oAuthUser.getReqGatewayType());
			autoCompleteRequestParentVO.setSourceType(oAuthUser.getSourceType());
			


			// OAuthenticationUtil.validateTokenApi(headers);

			HashMap requestMap = new HashMap<>();
			
			requestMap.put("MSISDNTOSEARCH", autoCompleteRequestParentVO.getData().getMsisdnToSearch());
			requestMap.put("LOGINIDTOSEARCH", autoCompleteRequestParentVO.getData().getLoginidToSearch());
			requestMap.put("USERNAMETOSEARCH", autoCompleteRequestParentVO.getData().getUsernameToSearch());
			requestMap.put("DOMAINCODE",  autoCompleteRequestParentVO.getData().getDomainCode());
			requestMap.put("CATEGORYCODE",  autoCompleteRequestParentVO.getData().getCategoryCode());

			if(!BTSLUtil.isNullString(autoCompleteRequestParentVO.getData().getSpecificSearch())) {
				requestMap.put("SPECIFIC_SEARCH",  "Y");
			}else {
				requestMap.put("SPECIFIC_SEARCH",  "N");
			}
			
			RequestVO p_RequestVO = new RequestVO();
			p_RequestVO.setRequestMap(requestMap);
			p_RequestVO.setRequestGatewayCode("REST");
			p_RequestVO.setRequestMessage(PretupsRestUtil.convertObjectToJSONString(autoCompleteRequestParentVO));
			this.process(p_RequestVO);
			if(p_RequestVO.isSuccessTxn())
			{
				responseObj = (List<AutoCompleteUserDetailsResponseVO>) p_RequestVO.getResponseMap().get("responseObject");
				autoCompleteResponseVO.setUserDetails(responseObj);
				autoCompleteResponseVO.setStatus(200);
				autoCompleteResponseVO.setMessageCode(p_RequestVO.getMessageCode());
				autoCompleteResponseVO.setMessage("Success");
			}
			else
			{
				autoCompleteResponseVO.setUserDetails(responseObj);
				autoCompleteResponseVO.setStatus(400);
				autoCompleteResponseVO.setMessageCode(p_RequestVO.getMessageCode());
				autoCompleteResponseVO.setMessage(p_RequestVO.getSenderReturnMessage());
			}
			
			return autoCompleteResponseVO;

		} catch (BTSLBaseException be) {

			_log.error(methodName, "BTSLBaseException " + be.getMessage());
			_log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} 
			autoCompleteResponseVO.setMessageCode(be.getMessageKey());
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					be.getMessageKey(), null);

			autoCompleteResponseVO.setStatus(400);
			autoCompleteResponseVO.setMessage(resmsg);
			return autoCompleteResponseVO;

		} catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			autoCompleteResponseVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
					PretupsErrorCodesI.REQ_NOT_PROCESS, null);
			autoCompleteResponseVO.setMessage(resmsg);
			return autoCompleteResponseVO;
		}
		finally {
		
			LogFactory.printLog(methodName, " Exited ", _log);
		}

	}

	private void userDetailsGeneration(Connection con,AutoCompleteUserDetailsRequestVO request, int userDetailsCount,
			List<AutoCompleteUserDetailsResponseVO> userDetailsList, int minLength) throws BTSLBaseException, Exception {

		final String methodName = "userDetailsGeneration";
		UserDAO userDao = new UserDAO();
		
		try {


			channelUserVO = userDao.loadAllUserDetailsByLoginID(con, loginID);

			userDao.autoCompleteUserDetails(con, request, userDetailsCount, userDetailsList, minLength);
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Error occured", "Exception " + e);
		  }
	}

	@Override
	public void process(RequestVO p_requestVO) {

		/*
		 * Authentication
		 * @throws BTSLBaseException
		 */
		final String methodName = "process";
		PretupsResponse<JsonNode> jsonReponse = new PretupsResponse<JsonNode>();;
		JsonNode dataObject = null;
		HashMap responseMap = new HashMap();
		Gson gson = new Gson();
		AutoCompleteUserDetailsRequestVO autoCompleteUserDetailsRequestVO = new AutoCompleteUserDetailsRequestVO();
		AutoCompleteUserDetailsData reqMsgObj = null;
		StringBuffer responseStr = new StringBuffer("");
		MComConnectionI mcomCon = null;
		Connection con = null;
		List<AutoCompleteUserDetailsResponseVO> userDetailsList = new ArrayList<AutoCompleteUserDetailsResponseVO>();
		try
		{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			String msisdn="";
			Boolean isLoginIDSearch = false;
			String loginId="";
			String userName = "";
			String domain = "";
			String category = "";
			int userDetailsCount= (Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.AUTOCOMPLETE_USER_DETAILS_COUNT);
			int minLength = (Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.MIN_LENGTH_TO_AUTOCOMPLETE);
			HashMap reqMap = p_requestVO.getRequestMap();
			if(reqMap.get("MSISDNTOSEARCH") != null ) {
				msisdn= ((String) reqMap.get("MSISDNTOSEARCH")); }
			if(reqMap.get("LOGINIDTOSEARCH") != null ) {
				loginId =((String) reqMap.get("LOGINIDTOSEARCH"));
			}
			if(reqMap.get("USERNAMETOSEARCH") != null ) {
				userName =((String) reqMap.get("USERNAMETOSEARCH"));
			}
			if(reqMap.get("DOMAINCODE") != null ) {
				domain =((String) reqMap.get("DOMAINCODE"));
			}
			if(reqMap.get("CATEGORYCODE") != null ) {
				category =((String) reqMap.get("CATEGORYCODE"));
			}

			boolean isSpecificSeach = false;
			if(reqMap.get("SPECIFIC_SEARCH").equals("Y")) isSpecificSeach = true;

			if(reqMap != null && (msisdn.length() >=minLength || loginId.length() >= minLength || userName.length() >= minLength) ) {

				if ("REST".equalsIgnoreCase(p_requestVO.getRequestGatewayCode())) {
					reqMsgObj = (gson.fromJson(p_requestVO.getRequestMessage(), AutoCompleteRequestParentVO.class)).getData();

					autoCompleteUserDetailsRequestVO.setMsisdn2(reqMsgObj.getMsisdnToSearch());
					autoCompleteUserDetailsRequestVO.setLoginId2(reqMsgObj.getLoginidToSearch());
					autoCompleteUserDetailsRequestVO.setUserName2(reqMsgObj.getUsernameToSearch());
					autoCompleteUserDetailsRequestVO.setDomain(reqMsgObj.getDomainCode());
					autoCompleteUserDetailsRequestVO.setCategory(reqMsgObj.getCategoryCode());
					autoCompleteUserDetailsRequestVO.setSpecificSearch(isSpecificSeach);
				}
				else {

					if (reqMap != null) {
						autoCompleteUserDetailsRequestVO.setMsisdn2(msisdn);
						autoCompleteUserDetailsRequestVO.setLoginId2(loginId);
						autoCompleteUserDetailsRequestVO.setUserName2(userName);
						autoCompleteUserDetailsRequestVO.setDomain(domain);
						autoCompleteUserDetailsRequestVO.setCategory(category);
						autoCompleteUserDetailsRequestVO.setSpecificSearch(isSpecificSeach);
					}
				}
				//if(reqMap.get("LOGINIDTOSEARCH") != null )
				if(reqMap.get("LOGINIDTOSEARCH") != null && !reqMap.get("LOGINIDTOSEARCH").equals(""))
				{
					isLoginIDSearch = true;
				}
				//Dao call

//					userDetailsGeneration(con,autoCompleteUserDetailsRequestVO, userDetailsCount, userDetailsList,
//							minLength);
//					_log.debug("Size of autocomplete list before filter is:" + userDetailsList.size(), methodName);

				UserDAO userDao = new UserDAO();
				channelUserVO = userDao.loadAllUserDetailsByLoginID(con, loginID);

				try
				{

					ArrayList list = new ArrayList();
					String toSearch = null;

					if(isLoginIDSearch)
					{
						if(isSpecificSeach) toSearch = (String) reqMap.get("LOGINIDTOSEARCH");
						else	toSearch = "%" + reqMap.get("LOGINIDTOSEARCH")  +"%";
					}
					else
					{
						if(isSpecificSeach) toSearch = (String) reqMap.get("USERNAMETOSEARCH");
						else	toSearch = "%" + reqMap.get("USERNAMETOSEARCH")  +"%";
					}


					if(channelUserVO.getUserType().equals(PretupsI.USER_TYPE_CHANNEL))
					{
						final ChannelTransferRuleDAO channelTransferRuleDAO = new ChannelTransferRuleDAO();
						final ChannelTransferRuleVO channelTransferRuleVO = channelTransferRuleDAO.loadTransferRule(con,
								channelUserVO.getNetworkID(), reqMsgObj.getDomainCode(),
								channelUserVO.getCategoryCode(), reqMsgObj.getCategoryCode(),
								PretupsI.TRANSFER_RULE_TYPE_CHANNEL, false);

						// user search for parent category

						if(isLoginIDSearch) {
							list = ChannelUserBL.loadChannelUserForXfrWithXfrRuleForLoginID(con, channelTransferRuleVO,
									reqMsgObj.getCategoryCode(), toSearch, channelUserVO);
						}else {//this is for userName
							list = ChannelUserBL.loadChannelUserForXfrWithXfrRule(con, channelTransferRuleVO,
									reqMsgObj.getCategoryCode(), toSearch, channelUserVO);
						}
					}
					else
					{
						ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
						list = channelUserWebDAO.loadCategoryUsersWithinGeoDomainHirearchyAutoComplete(con, autoCompleteUserDetailsRequestVO.getCategory(), channelUserVO.getNetworkID(), toSearch, "NA",
								reqMsgObj.getGeoDomainCode(), channelUserVO.getUserID());

					}
					_log.debug("Size of transfer rule list after edit is:" + list.size(), methodName);

					Map<String, String> listMap = new HashMap<String, String>();

					userDetailsList = list;

//					for (Object listObj : list)
//					{
//						ListValueVO listValueObj = (ListValueVO) listObj;
//						listMap.put(listValueObj.getValue(), listValueObj.getLabel());
//					}
//
//
//					Iterator<AutoCompleteUserDetailsResponseVO> it = userDetailsList.iterator();
//					while (it.hasNext()) {
//
//						AutoCompleteUserDetailsResponseVO responseObj = it.next();
//						boolean isFound = false;
//						String userIDObj = responseObj.getUserID();
//
//						if(listMap.get(userIDObj)!= null)
//						{
//							isFound = true;
//						}
//						if (!isFound) {
//							it.remove();
//						}
//					}
//					_log.debug("Size of autocomplete list after filter is:"+ userDetailsList.size(), methodName);
//
//					if ( userDetailsList.size() > userDetailsCount )
//					{
//						userDetailsList.subList(userDetailsCount, userDetailsList.size()).clear();
//					}

				}
				catch (Exception e) {
					throw new BTSLBaseException(this, methodName, "Error occured", "Exception " + e);
				} finally {
					if (mcomCon != null) {
						mcomCon.close("AutoCompleteUserDetailsController#userDetailsGeneration");
						mcomCon = null;
					}
				}


				int userDetailsListSize =userDetailsList.size();
				if (PretupsI.MOBILE_APP_GATEWAY.equalsIgnoreCase(p_requestVO.getRequestGatewayCode())){

					String resType = null;
					resType = reqMap.get("TYPE") + "RES";
					responseStr.append("{ \"type\": \"" + resType + "\" ,");
					responseStr.append(" \"txnStatus\": \"" + PretupsI.TXN_STATUS_SUCCESS + "\" ,");
					responseStr.append(" \"Date\": \"" +new SimpleDateFormat("dd/MM/YY").format(p_requestVO.getCreatedOn()) + "\" ,");
					if(userDetailsListSize != 0) {
						responseStr.append(" \"Message\": \"" + "Transaction has been completed" + "\" ,");
					}else {
						responseStr.append(" \"Message\": \"" + "No Detail found" + "\" ,");
					}
					responseStr.append(" \"userDetails\": [ ");
					int i=0;
					for(AutoCompleteUserDetailsResponseVO userDetailsItr: userDetailsList ) {
						i++;
						responseStr.append("{ \"msisdn\": \"" + userDetailsItr.getMsisdn() + "\" ,");
						responseStr.append(" \"loginId\": \"" + userDetailsItr.getLoginId() + "\" ,");
						if(i== userDetailsListSize ) {
							responseStr.append(" \"userName\": \"" + userDetailsItr.getUserName() + "\" }");
						}else {
							responseStr.append(" \"userName\": \"" + userDetailsItr.getUserName() + "\" },");
						}
					}
					responseStr.append("]}");
					responseMap.put("RESPONSE", responseStr);
					p_requestVO.setResponseMap(responseMap);
				}
				else {
					p_requestVO.setResponseMap(responseMap);
					dataObject = (JsonNode) PretupsRestUtil.convertJSONToObject(
							PretupsRestUtil.convertObjectToJSONString(userDetailsList), new TypeReference<JsonNode>() {
							});
					jsonReponse.setDataObject(dataObject);
					p_requestVO.setJsonReponse(jsonReponse);

					responseMap.put("responseObject", userDetailsList);

				}
				p_requestVO.setSuccessTxn(true);
				if(userDetailsListSize !=0) {
					p_requestVO.setMessageCode(PretupsErrorCodesI.USER_HIERRACHY_SUCCESS);
					p_requestVO.setSenderReturnMessage("Transaction has been completed!");
				}else {
					p_requestVO.setMessageCode(PretupsErrorCodesI.NO_DETAIL_FOUND);
					p_requestVO.setSenderReturnMessage("No Details found");
				}

			}
			else {
				if( msisdn.length() ==0 && loginId.length() ==0 && userName.length() ==0 ){
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.PROVIDE_AT_LEAST_ONE);
					p_requestVO.setSenderReturnMessage("Please provide at least one of the msisdn or loginId or userName!");

				}
				else {
					p_requestVO.setSuccessTxn(false);
					p_requestVO.setMessageCode(PretupsErrorCodesI.MIN_LENGTH_REQUIRED);
					String[] errorArg = { Integer.toString(minLength), "usernameToSearch/loginidToSearch/msisdnToSearch"};
					p_requestVO.setMessageArguments(errorArg);
				}
			}

		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			_log.error(methodName, "BTSLBaseException " + be.getMessage());
			_log.errorTrace(methodName, be);
			if (be.isKey()) {
				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				return;
			}
		} catch (Exception e) {
			p_requestVO.setSuccessTxn(false);
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESS, "", "", "",
					"Exception:" + e.getMessage());
			p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			return;
		}

		finally {
			LogFactory.printLog(methodName, " Exited ", _log);
		}

	}



}




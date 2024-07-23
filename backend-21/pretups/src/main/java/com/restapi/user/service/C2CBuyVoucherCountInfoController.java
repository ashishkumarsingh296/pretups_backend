package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;


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
import com.btsl.common.PretupsResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
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
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/*@Path("/voucher")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${C2CBuyVoucherCountInfoController.name}", description = "${C2CBuyVoucherCountInfoController.desc}")//@Api(tags= "Voucher Services")
@RestController
@RequestMapping(value = "/v1/voucher")
public class C2CBuyVoucherCountInfoController {
	
	
	public static final Log log = LogFactory.getLog(C2CBuyVoucherCountInfoController.class.getName());

	/**
	 * @(#)FetchChannelUserDetailsController.java This method gets the channel users
	 *                                            list
	 * 
	 * @return
	 * @throws IOException
	 * @throws BTSLBaseException
	 */
	@GetMapping(value="/getvouchercounts", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation( value = "View available voucher count",response = GetChannelUsersListResponseVo.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = GetChannelUsersListResponseVo.class),
	        @ApiResponse(code = 400, message = "Bad Request" ),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${getvouchercounts.summary}", description="${getvouchercounts.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PretupsResponse.class))
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


	public PretupsResponse<List<C2CVoucherCountResponseVO>>getVoucherCountInfo(
			@Parameter(description = "User ID",required = true) @RequestParam("userID") String userID,
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers, HttpServletResponse response1 )
			throws IOException, SQLException, BTSLBaseException {

			final String methodName =  "getVoucherCountInfo";
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			Connection con = null;
	        MComConnectionI mcomCon = null;
	        UserDAO userDao = new UserDAO();
	        ChannelUserVO channelUserVO = null;
	        String networkCode = "";
	        PretupsResponse<List<C2CVoucherCountResponseVO>> response =  new PretupsResponse<List<C2CVoucherCountResponseVO>>();
	        List<C2CVoucherCountResponseVO> voucherCountResp = new ArrayList<C2CVoucherCountResponseVO>();
	
			String messageArray[] = new String[1];
	
			
				try 
				{	
					mcomCon = new MComConnection();
		            con=mcomCon.getConnection();
			       
		            /*
					 * Authentication
					 * @throws BTSLBaseException
					 */
		            OAuthUser oAuthUserData=new OAuthUser();
		            oAuthUserData.setData(new OAuthUserData());
		            
		            OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
		            
					channelUserVO = userDao.loadUserDetailsFormUserID(con, userID);
					
			
					
					if (channelUserVO == null) {
						response.setResponse(PretupsI.RESPONSE_FAIL, false, "invalid login credentials");
						return response;
					}
					
					networkCode =  channelUserVO.getNetworkID();
					
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
		            
		            List<CardGroupDetailsVO> categoryList = new VomsVoucherDAO().returnVoucherDetailsWithCount
		            		(con, channelUserVO.getUserID(), new String[0], channelUserVO.getNetworkID());
		            
		            if (categoryList.isEmpty()) {
		            	String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
						String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
						Locale locale = new Locale(defaultLanguage, defaultCountry);
						String resmsg = RestAPIStringParser.getMessage(locale,  PretupsErrorCodesI.NO_VOUCHER_PRESENT, messageArray);
						response.setMessageCode(PretupsErrorCodesI.NO_VOUCHER_PRESENT);

		            	response.setResponse(PretupsI.RESPONSE_FAIL, false, resmsg);
		            	 response1.setStatus(HttpStatus.SC_BAD_REQUEST);

		            	return response;
					}
					HashMap<String,C2CVoucherCountResponseVO > responseObj = new HashMap<String,C2CVoucherCountResponseVO >();
					
					for (int i = 0; i < categoryList.size(); i++) 
					{
						CardGroupDetailsVO vomsCategoryVO = (CardGroupDetailsVO) categoryList.get(i);
						
						
						
						if(responseObj.get(vomsCategoryVO.getVoucherType()) == null)
						{
							C2CVoucherCountResponseVO c2CVoucherInfoResponseVO = new C2CVoucherCountResponseVO();
							VoucherSegmentCountResponse voucherSegmentResponse = new VoucherSegmentCountResponse();
							List<VoucherProfile> voucherProfileList = new ArrayList<VoucherProfile>();
							VoucherProfile voucherProfile = new VoucherProfile();
							
							List<VoucherSegmentCountResponse>voucherSegmentList = new ArrayList<VoucherSegmentCountResponse>();
							
							//c2CVoucherInfoResponseVO.getSegment().add(voucherSegmentResponse);
							
							voucherSegmentList.add(voucherSegmentResponse);
							
							c2CVoucherInfoResponseVO.setSegment(voucherSegmentList);
							
							c2CVoucherInfoResponseVO.setVoucherType(vomsCategoryVO.getVoucherTypeDesc());
							c2CVoucherInfoResponseVO.setVoucherName(vomsCategoryVO.getVoucherType());
							
							voucherSegmentResponse.setSegmentType(vomsCategoryVO.getVoucherSegment());
							voucherSegmentResponse.setSegmentValue(BTSLUtil.getSegmentDesc(vomsCategoryVO.getVoucherSegment()));
							
							int totalAmount = Integer.parseInt(vomsCategoryVO.getAvailableVouchers())* 
												Integer.parseInt(vomsCategoryVO.getVoucherDenomination());
							voucherProfile.setDenomination(vomsCategoryVO.getVoucherDenomination());
							voucherProfile.setVoucherProfileName(vomsCategoryVO.getProductName());
							voucherProfile.setVoucherProfileID(vomsCategoryVO.getVoucherProductId());
							voucherProfile.setNoOfVouchersAvailable(vomsCategoryVO.getAvailableVouchers());
							voucherProfile.setTotalAmount(String.valueOf(totalAmount));
							
							voucherProfileList.add(voucherProfile);
							voucherSegmentResponse.setVoucherDetails(voucherProfileList);
							
							responseObj.put(vomsCategoryVO.getVoucherType(), c2CVoucherInfoResponseVO);
							
						}
						else
						{
							C2CVoucherCountResponseVO c2CVoucherInfoResponseVO =responseObj.get(vomsCategoryVO.getVoucherType());
							
							boolean segmentExists = false;
							List<VoucherSegmentCountResponse> segmentList = c2CVoucherInfoResponseVO.getSegment();
							
							for(VoucherSegmentCountResponse responseListObj : segmentList)
							{
								if(responseListObj.getSegmentType().equals(vomsCategoryVO.getVoucherSegment()))
								{
									segmentExists = true;
									
									VoucherProfile voucherProfile = new VoucherProfile();
									
									int totalAmount = Integer.parseInt(vomsCategoryVO.getAvailableVouchers())* 
											Integer.parseInt(vomsCategoryVO.getVoucherDenomination());
									voucherProfile.setDenomination(vomsCategoryVO.getVoucherDenomination());
									voucherProfile.setVoucherProfileName(vomsCategoryVO.getProductName());
									voucherProfile.setVoucherProfileID(vomsCategoryVO.getVoucherProductId());
									voucherProfile.setNoOfVouchersAvailable(vomsCategoryVO.getAvailableVouchers());
									voucherProfile.setTotalAmount(String.valueOf(totalAmount));
									
									responseListObj.getVoucherDetails().add(voucherProfile);
								}
							}
							if(!segmentExists)
							{
								VoucherSegmentCountResponse newElement = new VoucherSegmentCountResponse();
								
								newElement.setSegmentType(vomsCategoryVO.getVoucherSegment());
								newElement.setSegmentValue(BTSLUtil.getSegmentDesc(vomsCategoryVO.getVoucherSegment()));
								
								List<VoucherProfile> denoms = new ArrayList<VoucherProfile>();
								
								VoucherProfile voucherProfile = new VoucherProfile();
								
								int totalAmount = Integer.parseInt(vomsCategoryVO.getAvailableVouchers())* 
										Integer.parseInt(vomsCategoryVO.getVoucherDenomination());
								voucherProfile.setDenomination(vomsCategoryVO.getVoucherDenomination());
								voucherProfile.setVoucherProfileName(vomsCategoryVO.getProductName());
								voucherProfile.setVoucherProfileID(vomsCategoryVO.getVoucherProductId());
								voucherProfile.setNoOfVouchersAvailable(vomsCategoryVO.getAvailableVouchers());
								voucherProfile.setTotalAmount(String.valueOf(totalAmount));
								
								denoms.add(voucherProfile);
								newElement.setVoucherDetails(denoms);
								
								c2CVoucherInfoResponseVO.getSegment().add(newElement);
								
							}
							
						}

					}
		            
					 for (Entry<String, C2CVoucherCountResponseVO> entry : responseObj.entrySet()) 
					 {
						 voucherCountResp.add(entry.getValue());
					 }
					 
					 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, voucherCountResp);
		            
				}
				catch (BTSLBaseException be) {
					log.error(methodName, "Exception:e=" + be);
					log.errorTrace(methodName, be);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
			          	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
			          }
			           else{
			           response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			           }
					String resmsg = RestAPIStringParser.getMessage(
							new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessageKey(),
							be.getArgs());
					response.setMessageCode(be.getMessage());
					response.setMessage(resmsg);

				} 
			catch(Exception e)
			{
				 response.setDataObject(PretupsI.RESPONSE_FAIL, false,null);
				  response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
				finally {
		        	if(mcomCon != null)
		        	{
		        		mcomCon.close("C2CBuyVoucherCountController#"+methodName);
		        		mcomCon=null;
		        		}
		            if (log.isDebugEnabled()) {
		               log.debug(methodName, "Exiting");
		            }
		        }     
			return response;

	 }
		
	}

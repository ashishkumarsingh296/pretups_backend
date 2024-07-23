package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;


import jakarta.servlet.http.HttpServletRequest;
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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsDAO;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.GetChannelUsersListResponseVo;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.web.user.businesslogic.UserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${GetStaffUserController.name}", description = "${GetStaffUserController.desc}")//@Api(tags= "Staff User List", value="Staff User Services")
@RestController
@RequestMapping(value = "/v1/staffUser")
public class GetStaffUserController {
	protected final Log log = LogFactory.getLog(getClass().getName());
	@GetMapping(value= "/getStaffUser", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(value = "Get Staff Users",response = GetChannelUsersListResponseVo.class,
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = GetChannelUsersListResponseVo.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${getStaffUser.summary}", description="${getStaffUser.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetChannelUsersListResponseVo.class))
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


	public GetChannelUsersListResponseVo getStaffUser(HttpServletRequest httpServletRequest,@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			HttpServletResponse response,@Parameter(description = "Enter UserName") @RequestParam("UserName") Optional<String> userName,@Parameter(description = "Enter Msisdn") @RequestParam("Msisdn") Optional<String> msisdnNo)throws BTSLBaseException, SQLException, JsonParseException,JsonMappingException, IOException {
		final String methodName = "getStaffUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        Connection con = null;
	    MComConnectionI mcomCon = null;
	    GetChannelUsersListResponseVo fileDownloadResponse = new GetChannelUsersListResponseVo();
        OAuthUser oAuthUser= null;
        OAuthUserData oAuthUserData =null;
        try{
        	ArrayList lookupList = new ArrayList();
        	LookupsDAO lookupsDAO = new LookupsDAO();
        	mcomCon = new MComConnection();
	    	con=mcomCon.getConnection();
	    	ArrayList list = new ArrayList();
	    	oAuthUser = new OAuthUser();
	    	oAuthUserData =new OAuthUserData();
	    	oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response);
			ChannelUserVO channelUserVO = new UserDAO().loadAllUserDetailsByLoginID(con, oAuthUser.getData().getLoginid());

        		list=new UserWebDAO().loadStaffUserList(con, channelUserVO.getUserID(), PretupsI.STAFF_USER_TYPE, "%%%", channelUserVO.getUserType());
				if (userName.isPresent() || msisdnNo.isPresent()) {
					ArrayList<ChannelUserVO> staffUserList1 = new ArrayList<ChannelUserVO>();

					for (int j = 0; j < list.size(); j++) {

						ChannelUserVO channelUser = (ChannelUserVO) list.get(j);
						if (userName.isPresent() && channelUser.getUserName().equals(userName.get())
								|| msisdnNo.isPresent() && !BTSLUtil.isNullString(channelUser.getMsisdn()) && channelUser.getMsisdn().equals(msisdnNo.get()))
						{
							staffUserList1.add(channelUser);

						}
					}
					{
						if (staffUserList1.size() > 0) {
							for (int i = 0; i < staffUserList1.size(); i++) {
								ChannelUserVO userVO = (ChannelUserVO) staffUserList1.get(i);
								lookupList = lookupsDAO.loadLookupsFromLookupCode(con, userVO.getStatus(),
										PretupsI.USER_STATUS_TYPE);
								LookupsVO lookupsVO = (LookupsVO) lookupList.get(0);
								userVO.setStatusDesc(lookupsVO.getLookupName());
							}

							fileDownloadResponse.setStaffUserList(staffUserList1);
                            fileDownloadResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
							fileDownloadResponse.setMessage("List fetched successfully");
						} else {
							fileDownloadResponse.setMessage("No Staff User found");
							fileDownloadResponse.setStatus(String.valueOf(200));
						}
					}
				} else {
        		
        	if(list.size()>0)
        	{
        		
        		for(int i=0;i<list.size();i++)
        		{
					ChannelUserVO userVO = (ChannelUserVO) list.get(i);
					lookupList = lookupsDAO.loadLookupsFromLookupCode(con, userVO.getStatus(), PretupsI.USER_STATUS_TYPE);

					if (lookupList != null && !lookupList.isEmpty()) {
						LookupsVO lookupsVO = (LookupsVO) lookupList.get(0);
						userVO.setStatusDesc(lookupsVO.getLookupName());
					}
				}
        		
        		
        			
        		
//        		fileDownloadResponse.setStaffUserList(staffUserList1);
        		fileDownloadResponse.setStaffUserList(list);
        		fileDownloadResponse.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
        		fileDownloadResponse.setMessage("List fetched successfully");
        	}
        	else
        	{
        		fileDownloadResponse.setMessage("No Staff User found");
        		fileDownloadResponse.setStatus(String.valueOf(204));
        	}
        	fileDownloadResponse.setService("getStaffUserResp");
        	return fileDownloadResponse;
        }
        }
        
        catch (BTSLBaseException be) {
          	 log.error(methodName, "Exception:e=" + be);
               log.errorTrace(methodName, be);
               if(be.getMessage().equalsIgnoreCase("1080001")||be.getMessage().equalsIgnoreCase("1080002")||be.getMessage().equalsIgnoreCase("1080003")||
               		 be.getMessage().equalsIgnoreCase("241023")||be.getMessage().equalsIgnoreCase("241018")){
               	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
               	 fileDownloadResponse.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
               }
                else{
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                fileDownloadResponse.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
                }
               String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
               String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
       	   String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), be.getMessage(), null);
       	   fileDownloadResponse.setMessageCode(be.getMessage());
       	   fileDownloadResponse.setMessage(resmsg);
       	fileDownloadResponse.setService("getStaffUserResp");
        	}
        catch (Exception ex) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
			fileDownloadResponse.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			fileDownloadResponse.setService("getStaffUserResp");
	        log.errorTrace(methodName, ex);
	        log.error(methodName, "Unable to write data into a file Exception = " + ex.getMessage());
	    }
        finally {
	    	if (mcomCon != null) {
				mcomCon.close("GetStaffUserController#getStaffUser");
				mcomCon = null;
			}
	        if (log.isDebugEnabled()) {
	            log.debug("getReportUser", " Exited ");
	        }
	    }

        
	
        return fileDownloadResponse;
	}
}

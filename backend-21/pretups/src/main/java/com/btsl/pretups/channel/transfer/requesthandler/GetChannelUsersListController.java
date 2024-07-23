package com.btsl.pretups.channel.transfer.requesthandler;


import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

import io.swagger.v3.oas.annotations.Parameter;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.GetChannelUsersListResponseVo;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${GetChannelUsersListController.name}", description = "${GetChannelUsersListController.desc}")//@Api(tags= "Channel Users", value="Fetch Channel Users List")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class GetChannelUsersListController {
public static final Log log = LogFactory.getLog(GetChannelUsersListController.class.getName());
	
/**
 * this method gets the channel users list
 * @param networkCode
 * @param identifiertype
 * @param identifiervalue
 * @param userName
 * @param userCategoryCode
 * @param userGeography
 * @param userDoaminCode
 * @return
 * @throws IOException
 * @throws SQLException
 * @throws BTSLBaseException
 */
/*@GET
@Path("/channelUsersList")*/
@GetMapping(value= "/channelUsersList", produces = MediaType.APPLICATION_JSON)
@ResponseBody
//@Produces(MediaType.APPLICATION_JSON)
/*@ApiOperation(tags= "Channel Users", value = "Fetch Channel Users List",
             notes=("Api Info:") + ("\n") + ("Provide ALL if you want to get all users"),response = GetChannelUsersListResponseVo.class,
             authorizations = {
                 @Authorization(value = "Authorization")})
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = GetChannelUsersListResponseVo.class),
        @ApiResponse(code = 400, message = "Bad Request" ),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 404, message = "Not Found")
        })*/


@io.swagger.v3.oas.annotations.Operation(summary = "${channelUsersList.summary}", description="${channelUsersList.description}",

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




public GetChannelUsersListResponseVo getChannelUsersList(
@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,

/*@Parameter(description = "Network Code", required = true)
@RequestParam("networkCode") String networkCode,*/
//@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true,allowableValues = "LOGINID,MSISDN")
//@RequestParam("identifierType") String identifiertype,
//@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_VALUE, required = true)
//@RequestParam("identifierValue") String identifiervalue,
/*@Parameter(description = "Msisdn Of The Channel User To Search")
@RequestParam(value="msisdn") Optional<String> msisdn2Optional,
@Parameter(description = "LoginId Of The Channel User To Search")
@RequestParam(value="loginId") Optional<String> loginId2Optional,
@Parameter(description = "User Name Of The Channel User To Search.")
@RequestParam(value="userName") Optional<String> userNameOptional,
*/
@Parameter(description = "User Name for Channel User To Search.")
@RequestParam("userName") Optional<String> userName,
@Parameter(description = "Msisdn for Channel User To Search.")
@RequestParam("msisdn") Optional<String> msisdnNo,
@Parameter(description = "Category Code Of The Channel User To Search.")
@RequestParam("category") Optional<String> userCategoryNameOptional,
@Parameter(description = "Geography Code Of The Channel User To Search")
@RequestParam("geography") Optional<String> userGeographyOptional,
@Parameter(description = "Domain Code Of The Channel User To Search.")
@RequestParam("domain") Optional<String> userDoaminOptional,
@Parameter(description = "Status Code for which you want to search ")
@RequestParam("status") Optional<String> status,
@Parameter(description = "Network Code")
@RequestParam("externalNetworkCode") Optional<String> extNetworkCode,
/*@Parameter(description = "The number of records , user wants to fetch/view in one page")
 @RequestParam("entriesPerPage") Optional<String> toRowOptional,*/
 HttpServletResponse response1 
		)throws IOException, SQLException, BTSLBaseException {
	final String methodName =  "getChannelUsersList";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered ");
	}
	
	
	String identifierValue = null;
	
	
	
	
	Connection con = null;
    MComConnectionI mcomCon = null;
    GetChannelUsersListResponseVo response=null;
   
    UserDAO userDao = new UserDAO();
    try {
    	response = new GetChannelUsersListResponseVo();
		OAuthUser oAuthUserData=new OAuthUser();
		//UserDAO userDao = new UserDAO();
		
		oAuthUserData.setData(new OAuthUserData());
		
		OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
		//response.setService("");
		
		String loginId =  oAuthUserData.getData().getLoginid();
		String msisdn =  oAuthUserData.getData().getMsisdn(); 
		
    	
    	identifierValue= loginId;
    	
    	//networkCode = SqlParameterEncoder.encodeParams(networkCode);
    	identifierValue = SqlParameterEncoder.encodeParams(identifierValue);
    	
    	
    	/*String msisdn2 = SqlParameterEncoder.encodeParams(msisdn2Optional.map(Object::toString).orElse(null));
    	String loginId2 =SqlParameterEncoder.encodeParams(loginId2Optional.map(Object::toString).orElse(null));
    	String userName = SqlParameterEncoder.encodeParams(userNameOptional.map(Object::toString).orElse(null));*/
        String userCategoryCode =SqlParameterEncoder.encodeParams(userCategoryNameOptional.map(Object::toString).orElse(null));
    	String userGeography =SqlParameterEncoder.encodeParams(userGeographyOptional.map(Object::toString).orElse(null));
    	String userDoaminCode = SqlParameterEncoder.encodeParams(userDoaminOptional.map(Object::toString).orElse(null));
    	String userStatus = SqlParameterEncoder.encodeParams(status.map(Object::toString).orElse(null));
    	String networkCode = SqlParameterEncoder.encodeParams(extNetworkCode.map(Object::toString).orElse(null));
		mcomCon = new MComConnection();
        con = mcomCon.getConnection();
		response = new GetChannelUsersListResponseVo();

		
		 String messageArray[] = new String[1];
		if(BTSLUtil.isNullString(networkCode)){
			response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			response.setMessageCode(PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK);
			response.setMessage("External network code value is blank.");
			return response;
		}
		

		
		if (!BTSLUtil.isNullString(networkCode)) {
            NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
            if(networkVO==null){
            	messageArray[0] =  networkCode;
           throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
            }
        }
		else
		{
			 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0,null,null);
		}
		
//		boolean validateuser = false;
//		PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
//		validateuser = pretupsRestUtil.validateUser(identifiertype, identifiervalue, networkCode, con);
//		if(validateuser == false){
//			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0,null,null);
//		}

		//validate msisdn2
		/*if(!BTSLUtil.isNullString(msisdn2)){
			UserVO userVO = null;
			UserDAO userDAO = new UserDAO();
			userVO = (UserVO) userDAO.loadUsersDetails(con, msisdn2);
			if(BTSLUtil.isNullObject(userVO)) {
				response.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
				 response.setMessage("Invalid MSISDN");
				 response.setStatus(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
		}*/
		
		//vaidate loginId2
		/*if(!BTSLUtil.isNullString(loginId2)){
			UserVO userVO = null;
			UserDAO userDAO = new UserDAO();
			userVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(con, loginId2);
			if(BTSLUtil.isNullObject(userVO)) {
				response.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_LOGINID);
				 response.setMessage("Invalid login ID.");
				  response.setStatus(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
		}*/
		
		/*if(!BTSLUtil.isNullString(userName)){

			if(BTSLUtil.isNullString(userDoamin)|| BTSLUtil.isNullString(userCategoryName) || BTSLUtil.isNullString(userGeography)) {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.USER_NAME_ERROR);
				response.setMessage("With Username Domain,Category,GeographyCode is mandatory.");
				return response;
			}
			
			
		}else {
			//if username is not given and either of three is given then it will throw error as all 4 need to be present
			
			if(!BTSLUtil.isNullString(userDoamin)||!BTSLUtil.isNullString(userCategoryName) || !BTSLUtil.isNullString(userGeography)) {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.USER_NAME_ERROR);
				response.setMessage("With Username Domain,Category,GeographyCode is mandatory.");
				return response;
			}
		}
		
		*/ 
		
		//validate userGeography
		GeographicalDomainDAO geographicalDomainDAO=new GeographicalDomainDAO();
		List<GeographicalDomainVO> domainParentList = new ArrayList();
		
		
		if(!BTSLUtil.isNullString(userGeography) && !userGeography.equalsIgnoreCase("ALL")){
			domainParentList=geographicalDomainDAO.loadGeoDomainList(con, networkCode, "N");
			if(BTSLUtil.isNullObject(domainParentList)) {
				response.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
				 response.setMessage("No Geography Associted With Given Network Code.");
				 response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
				return response;
				
			}
			
			int geoMatched=0;
			for( GeographicalDomainVO geoList : domainParentList) {
				if(userGeography.equals(geoList.getGrphDomainCode()))
				{
					geoMatched=1;
					break;
				}
			
			}
			if(geoMatched==0) {
			response.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
			 response.setMessage("Invalid geography");
			 response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
			return response;}
			
			
		}else {
			userGeography="ALL";
		}
		String userCategoryName="";
		//validate usercategory
		if(!BTSLUtil.isNullString(userCategoryCode) && !userCategoryCode.equalsIgnoreCase("ALL")){
			
			 userCategoryName=userDao.getCategoryNameFromCatCode(con, userCategoryCode, null);
			if(BTSLUtil.isNullString(userCategoryName)) {
				response.setMessageCode(PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY);
				 response.setMessage("Invalid category");
				 response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
				return response;
			}
		}else {
			userCategoryCode="ALL";
		}
		
		
		//validate Domain
		String domainName="";
		if(!BTSLUtil.isNullString(userDoaminCode)  && !userDoaminCode.equalsIgnoreCase("ALL")) {
			
			domainName=userDao.getDomainNameOrCode(con, userDoaminCode, null);
			if(BTSLUtil.isNullString(domainName)) {
				response.setMessageCode(PretupsErrorCodesI.DOMAIN_INVALID);
				 response.setMessage("Domain Entered is invalid.");
				 response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
				return response;
			}
			
		
		}else
		{
			userDoaminCode="ALL";
		}
		
		

		
/*		if(!BTSLUtil.isNullString(fromRow)&&!BTSLUtil.isNullString(toRow)) {
			if(!BTSLUtil.isNumeric(fromRow) || !BTSLUtil.isNumeric(toRow) ){
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.PAGINATION_VALUES_INVALID);
				response.setMessage("Invalid Values For PageNumber or EntriesperPage.");
				return response;
			}
			
			int fromRow2=Integer.parseInt(fromRow);
			int toRow2=Integer.parseInt(toRow);
			if(fromRow2>toRow2) {
				response.setStatus(false);
				response.setMessageCode(PretupsErrorCodesI.FROM_ROW_GREATER);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessage("fromRow is greater than toRow.");
				return response;
			}
			
			if(fromRow2<0 || toRow2 <0 ) {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.PAGINATION_VALUES_INVALID);
				response.setMessage("Invalid Values For PageNumber or EntriesperPage.");
				return response;
			}
		}else {
			if(BTSLUtil.isNullString(fromRow)&&!BTSLUtil.isNullString(toRow)) {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.PAGINATION_VALUES_REQ);
				response.setMessage("Please Provide both PageNumber & EntriesperPage.");
				return response;
			}
			else if(BTSLUtil.isNullString(toRow)&&!BTSLUtil.isNullString(fromRow)) {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.PAGINATION_VALUES_REQ);
				response.setMessage("Please Provide both PageNumber & EntriesperPage.");
				return response;
			}
			
		}
*/		
		GetChannelUsersListResponseVo getChannelUsersListResponseVo=new GetChannelUsersListResponseVo();
		String userId="";
		
		
		ChannelUserVO channelUserVO=new ChannelUserVO();
//		if(identifiertype.equalsIgnoreCase("LOGINID")) {
//			
//			channelUserVO = userDao.loadAllUserDetailsByLoginID(con, identifiervalue);
//			userId=channelUserVO.getUserID();
//		}
//		else if(identifiertype.equalsIgnoreCase("MSISDN")) {
//			channelUserVO = userDao.loadUserDetailsByMsisdn(con, identifiervalue);
//			userId=channelUserVO.getUserID();
//		}
		
		channelUserVO = userDao.loadAllUserDetailsByLoginID(con, identifierValue);
		if(channelUserVO.getUserType().equals(PretupsI.USER_TYPE_CHANNEL)) {
			userId = channelUserVO.getUserID();
		} else if (channelUserVO.getUserType().equals(PretupsI.USER_TYPE_STAFF)) {
			userId = channelUserVO.getParentID();
		}


		response=userDao.getChannelUsersList(con,userDoaminCode,userCategoryCode,userGeography,userId,userStatus,false);
		if (userName.isPresent() || msisdnNo.isPresent()) {

			ArrayList<GetChannelUsersMsg> channelUsersList1 = new ArrayList<GetChannelUsersMsg>();
			for (GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList()) {

				if (userName.isPresent() && getChannelUsersMsg.getUserName().equals(userName.get())
						|| msisdnNo.isPresent() && getChannelUsersMsg.getMsisdn().equals(msisdnNo.get())) {
					channelUsersList1.add(getChannelUsersMsg);

				}

			}

			response.setChannelUsersList(channelUsersList1);
			if (!BTSLUtil.isNullOrEmptyList(channelUsersList1)) {
				response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
				String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				Locale locale = new Locale(defaultLanguage, defaultCountry);			
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
			} else {
				response.setStatus(String.valueOf(200));
				response.setMessageCode(PretupsErrorCodesI.NO_CHNL_USER_FOUND);
				response.setMessage("NO Channel User Found With The Given Input.");
			}
		}
		else {
		if(!BTSLUtil.isNullOrEmptyList(response.getChannelUsersList())) {
			 response.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			 String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), PretupsErrorCodesI.SUCCESS, null);
	            response.setMessage(resmsg);

		}else {
			 response.setStatus(String.valueOf(204));
			
			
			/*if(!BTSLUtil.isNullString(fromRow)&&!BTSLUtil.isNullString(toRow)&& BTSLUtil.isNullString(msisdn2)&&BTSLUtil.isNullString(loginId2)&&BTSLUtil.isNullString(userName)) {
				response.setMessageCode(PretupsErrorCodesI.NO_RECORD_PAGE);
				response.setMessage("No Data Found on page "+fromRow+".");
				
				
			}
			else if(!BTSLUtil.isNullString(userName)) {
				response.setMessageCode(PretupsErrorCodesI.DETAIL_NOT_FOUND_WITH_USRNAMEORCATORDOM);
				response.setMessage("NO Channel User Found With The Given UserName and Detils(Category/Geography/Domain) provided with it.");
	
			}
			else {*/
				response.setMessageCode(PretupsErrorCodesI.NO_CHNL_USER_FOUND);
				response.setMessage("NO Channel User Found With The Given Input.");
			
		}
			return response;
			
		}
		
 }  catch (BTSLBaseException be) {
	 log.error(methodName, "Exception:e=" + be);
     log.errorTrace(methodName, be);
     
	    //String  btslMessage = BTSLUtil.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), null);
	   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), null);
	    //response.setStatus(false);
	response.setMessageCode(be.getMessage());
	response.setMessage(resmsg);
	if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())){
		response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
         response.setStatus(String.valueOf(HttpStatus.SC_UNAUTHORIZED));
    }
   else{
	   response1.setStatus(HttpStatus.SC_BAD_REQUEST);
   		response.setStatus(String.valueOf(HttpStatus.SC_BAD_REQUEST));
   }
}
    catch (Exception e) {
        log.error(methodName, "Exceptin:e=" + e);
        log.errorTrace(methodName, e);
        response.setStatus(String.valueOf(PretupsI.RESPONSE_FAIL));
      
		response.setMessageCode("error.general.processing");
		response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
    } finally {
    	try {
        	if (mcomCon != null) {
				mcomCon.close("GetChannelUsersList");
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
    log.debug(methodName, response);
	return response;
}
}

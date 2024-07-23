package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;

import javax.ws.rs.core.MediaType;

//import org.apache.struts.action.ActionForward;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.GetDomainCategoryMsg;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.GetDomainCatParentCatResp1Msg;
import com.btsl.user.businesslogic.GetDomainCatParentCatResp2Msg;
import com.btsl.user.businesslogic.GetDomainCategoryParentCatResponseVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.web.UserForm;

import io.swagger.v3.oas.annotations.Parameter;

/*@Path("v1/channelUsers")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${GetDomainCategoryParentController.name}", description = "${GetDomainCategoryParentController.desc}")//@Api(tags="Channel Users",value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class GetDomainCategoryParentController {
public static final Log log = LogFactory.getLog(GetChannelUsersListController.class.getName());
/**
 * This Method is used when giving domain/Category/Parent Category/parentUser while adding Channel User
 * @param networkCode
 * @param loginId
 * @param password
 * @return
 * @throws IOException
 * @throws SQLException
 * @throws BTSLBaseException
 */
	/*@GET
	@Path("/domainCategoryParentCat")*/
    @GetMapping(value ="/domainCategoryParentCat", produces = MediaType.APPLICATION_JSON)
    @ResponseBody
	/*@Produces(MediaType.APPLICATION_JSON)*/
	/*@ApiOperation(tags="Channel Users", value = "Get Domain/Category/Parent Category",response = GetDomainCategoryParentCatResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = GetDomainCategoryParentCatResponseVO.class),
	        @ApiResponse(code = 400, message = "Bad Request" ),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${domainCategoryParentCat.summary}", description="${domainCategoryParentCat.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GetDomainCategoryParentCatResponseVO.class))
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




	public GetDomainCategoryParentCatResponseVO getDomainCatParentCat(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
//			@Parameter(description = "networkCode", required = true)
//			@RequestParam("networkCode") String networkCode,
//			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true,allowableValues = "LOGINID,MSISDN")
//			@RequestParam("identifierType") String identifiertype,
//			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_VALUE, required = true)
//			@RequestParam("identifierValue") String identifiervalue
			/*@Parameter(description ="Category")
			@DefaultValue("") @QueryParam("category") String userSelectedCat,
			@Parameter(description ="Parent Category")
			@DefaultValue("") @QueryParam("parentCategory") String userSelectedParentCat,
			@Parameter(description ="Parent User")
			@DefaultValue("") @QueryParam("parentUser") String parentUser*/
//			@Parameter(description ="User Status")
//			@DefaultValue("ALL") @QueryParam("userStatus") String userStatus,
			HttpServletResponse response1 

	)throws IOException, SQLException, BTSLBaseException {

		final String methodName =  "getDomainCatParentCat";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
	    MComConnectionI mcomCon = null;
	    UserDAO userDao = new UserDAO();
	    
	    OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		String identifierValue = null;
	    
	    C2STransferDAO c2STransferDAO=new C2STransferDAO();
	    ChannelTransferRuleVO channelTransferRuleVO =new ChannelTransferRuleVO();
	    ChannelUserDAO ChannelUserDAO = new ChannelUserDAO();
	    ChannelUserVO channelUserVO = null;
	    GetDomainCategoryParentCatResponseVO response=null;
	    String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		Locale locale = new Locale(defaultLanguage, defaultCountry);	    
	    try {
	    	
	    	response = new GetDomainCategoryParentCatResponseVO();
		    oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			identifierValue= oAuthUser.getData().getLoginid();
		    identifierValue = SqlParameterEncoder.encodeParams(identifierValue);
		    
		   
		    
		    
			mcomCon = new MComConnection();
	        con = mcomCon.getConnection();
		
	        String messageArray[] = new String[1];
	        
			channelUserVO = userDao.loadAllUserDetailsByLoginID(con, identifierValue);
			String domainCode="";
			String domainCodeName="";
			String logdUserCategoryCode="";
			String logdUserCategoryName="";
			domainCode=channelUserVO.getDomainID();
        	domainCodeName=channelUserVO.getCategoryName();
        	logdUserCategoryCode=channelUserVO.getCategoryCode();

            final UserForm theForm = new UserForm();
            this.loadDomainList(theForm, channelUserVO);
        
        	if(BTSLUtil.isNullString(domainCodeName)) {
        		domainCodeName=userDao.getDomainNameOrCode(con, domainCode, null);
        	
        	}
        
        	logdUserCategoryName=userDao.getCategoryNameFromCatCode(con,logdUserCategoryCode,null);
        	
        	String ownerId=channelUserVO.getOwnerID();
        	ChannelUserVO ownerDet= userDao.loadUserDetailsFormUserID(con, ownerId);
        	response.setOwnerName(ownerDet.getUserName());
        	response.setOwnerMsisdn(ownerDet.getMsisdn());
        	response.setOwnerLoginId(ownerDet.getLoginID());
        	response.setOwnerUserId(ownerDet.getUserID());
        	response.setUserStatus(ownerDet.getStatus());
       
        	/* 	NOT IN USE 
	          //transfer rules dao loadC2SRulesListForChannelUserAssociation
	            
	     */
	          ArrayList<GetDomainCatParentCatResp1Msg> resp1=new ArrayList<>();
	          ArrayList<GetDomainCatParentCatResp2Msg> resp2=null;
	          ArrayList<GetDomainCatParentCatParentUserMsg> users=null;
	            
	   
	          GetDomainCatParentCatResp1Msg getDomainCatParentCatResp1Msg=null;
	  	      GetDomainCatParentCatResp2Msg getDomainCatParentCatResp2Msg=null;

	          GetDomainCategoryMsg getDomainCategoryMsg;
	          GetDomainCatParentCat getDomainCatParentCat;
	          
	          ArrayList allowedChildCategoryList =  theForm.getCategoryList();
	          if(!BTSLUtil.isNullString(logdUserCategoryName)) {
	          for(int i=0 ; i < allowedChildCategoryList.size() ; i++) {
	        	  resp2=new ArrayList<>();
	        	  
	        	  CategoryVO categoryVO= (CategoryVO)allowedChildCategoryList.get(i);
	        	  
	        	  getDomainCatParentCatResp1Msg= new GetDomainCatParentCatResp1Msg();
  				  getDomainCatParentCatResp1Msg.setCategoryName(categoryVO.getCategoryName());
  				  getDomainCatParentCatResp1Msg.setCategoryNameCode(categoryVO.getCategoryCode());
  				  getDomainCatParentCatResp1Msg.setMaxTxnMsisdn(categoryVO.getMaxTxnMsisdnInt());
  				  
  				  String[] categoryID = {categoryVO.getCategoryCode() , categoryVO.getDomainCodeforCategory() , Integer.toString(categoryVO.getSequenceNumber())};
  				  
  				  this.populateParentCategoryList(theForm , channelUserVO , categoryID);
  				  ArrayList parentCategoryList = theForm.getParentCategoryList();
  				
	        	  for(int j=0 ; j < theForm.getParentCategoryList().size() ; j++) {
	        		  CategoryVO parentCategoryVO= (CategoryVO)parentCategoryList.get(j);
	        		  users=new ArrayList();
      				  getDomainCatParentCatResp2Msg= new GetDomainCatParentCatResp2Msg();
      				  getDomainCatParentCatResp2Msg.setParentCategory(parentCategoryVO.getCategoryName());
      				  getDomainCatParentCatResp2Msg.setParentCategoryCode(parentCategoryVO.getCategoryCode());
      				  String parentCat=getDomainCatParentCatResp2Msg.getParentCategoryCode();
      				
      				if(logdUserCategoryCode.equalsIgnoreCase(parentCat)){
    	        		GetDomainCatParentCatParentUserMsg getDomainCatParentCatParentUserMsg=new GetDomainCatParentCatParentUserMsg();
    	        		getDomainCatParentCatParentUserMsg.setParentUserName(channelUserVO.getUserName());
    	        		getDomainCatParentCatParentUserMsg.setParentUserId(channelUserVO.getUserID());
    	        		getDomainCatParentCatParentUserMsg.setParentMsisdn(channelUserVO.getMsisdn());
    	        		getDomainCatParentCatParentUserMsg.setParentLoginId(channelUserVO.getLoginID());
    	        		getDomainCatParentCatParentUserMsg.setUserStatus(channelUserVO.getStatus());
    	        		users.add(getDomainCatParentCatParentUserMsg);
    	        			
    	        		
    	        	}else {
    	        		String userSelectedParentCatCode=userDao.getCategoryNameFromCatCode(con, null, parentCat);
    	        		
    	        		users=userDao.getUsersInHierachyWithCat(con, userSelectedParentCatCode, channelUserVO.getUserID(),"%");
    	        		
    	        	}
    				
    				getDomainCatParentCatResp2Msg.setParentUser(users);
    				resp2.add(getDomainCatParentCatResp2Msg);
	        	  }
	        	  
	        	  getDomainCatParentCatResp1Msg.setParentList(resp2);
  				  resp1.add(getDomainCatParentCatResp1Msg);
	          }
	          }
	          //original
//        	if(!BTSLUtil.isNullString(logdUserCategoryName)) {
//        	
//        		if(!logdUserCategoryName.equalsIgnoreCase("Retailer")) {
//        			for(int i=parentCatRule.indexOf(logdUserCategoryName)+2;i<parentCatRule.size();i=i+2) {
//        		
//        				resp2=new ArrayList<>();
//        				getDomainCatParentCatResp1Msg= new GetDomainCatParentCatResp1Msg();
//        				getDomainCatParentCatResp1Msg.setCategoryName(parentCatRule.get(i));
//        				getDomainCatParentCatResp1Msg.setCategoryNameCode(parentCatRule.get(i+1));
//        				String catSelected=getDomainCatParentCatResp1Msg.getCategoryName();
//        
//        				if(logdUserCategoryName.equalsIgnoreCase("Super Distributor")) {
//                			for(int j=0;j<parentCatRule.indexOf(catSelected);j=j+2) {
//                				users=new ArrayList();
//                				getDomainCatParentCatResp2Msg= new GetDomainCatParentCatResp2Msg();
//                				getDomainCatParentCatResp2Msg.setParentCategory(parentCatRule.get(j));
//                				getDomainCatParentCatResp2Msg.setParentCategoryCode(parentCatRule.get(j+1));
//                				String parentCat=getDomainCatParentCatResp2Msg.getParentCategory();
//                				
//                				if(logdUserCategoryCode.equalsIgnoreCase(parentCat)){
//                	        		GetDomainCatParentCatParentUserMsg getDomainCatParentCatParentUserMsg=new GetDomainCatParentCatParentUserMsg();
//                	        		getDomainCatParentCatParentUserMsg.setParentUserName(channelUserVO.getUserName());
//                	        		getDomainCatParentCatParentUserMsg.setParentUserId(channelUserVO.getUserID());
//                	        		getDomainCatParentCatParentUserMsg.setParentMsisdn(channelUserVO.getMsisdn());
//                	        		getDomainCatParentCatParentUserMsg.setParentLoginId(channelUserVO.getLoginID());
//                	        		users.add(getDomainCatParentCatParentUserMsg);
//                	        			
//                	        		
//                	        	}else {
//                	        		String userSelectedParentCatCode=userDao.getCategoryNameFromCatCode(con, null, parentCat);
//                	        		
//                	        		users=userDao.getUsersInHierachyWithCat(con, userSelectedParentCatCode, channelUserVO.getUserID(),"%");
//                	        		
//                	        	}
//                				
//                				getDomainCatParentCatResp2Msg.setParentUser(users);
//                				resp2.add(getDomainCatParentCatResp2Msg);
//                			}
//                		
//                		}else if(!logdUserCategoryName.equalsIgnoreCase("Super Distributor")){
//                			for(int j=2;j<parentCatRule.indexOf(catSelected);j=j+2) {
//                				users=new ArrayList();
//                				getDomainCatParentCatResp2Msg= new GetDomainCatParentCatResp2Msg();
//                				getDomainCatParentCatResp2Msg.setParentCategory(parentCatRule.get(j));
//                				getDomainCatParentCatResp2Msg.setParentCategoryCode(parentCatRule.get(j+1));
//                				String parentCat=getDomainCatParentCatResp2Msg.getParentCategory();
//                				if(logdUserCategoryCode.equalsIgnoreCase(parentCat)){
//                					GetDomainCatParentCatParentUserMsg getDomainCatParentCatParentUserMsg=new GetDomainCatParentCatParentUserMsg();
//                	        		getDomainCatParentCatParentUserMsg.setParentUserName(channelUserVO.getUserName());
//                	        		getDomainCatParentCatParentUserMsg.setParentUserId(channelUserVO.getUserID());
//                	        		getDomainCatParentCatParentUserMsg.setParentMsisdn(channelUserVO.getMsisdn());
//                	        		getDomainCatParentCatParentUserMsg.setParentLoginId(channelUserVO.getLoginID());
//                	        		users.add(getDomainCatParentCatParentUserMsg);
//                	        			
//                	        		
//                	        	}else {
//                	        		String userSelectedParentCatCode=userDao.getCategoryNameFromCatCode(con, null, parentCat);
//                	        		
//                	        		users=userDao.getUsersInHierachyWithCat(con, userSelectedParentCatCode, channelUserVO.getUserID(),"%");
//                	        		
//                	        	}
//                				
//                				getDomainCatParentCatResp2Msg.setParentUser(users);
//                				resp2.add(getDomainCatParentCatResp2Msg);
//                				
//                			}
//                		}
//        				getDomainCatParentCatResp1Msg.setParentList(resp2);
//        				resp1.add(getDomainCatParentCatResp1Msg);
//        				
//        				}
//        		
//        		}else {
//        			
//        			resp2=new ArrayList<>();
//        			getDomainCatParentCatResp1Msg= new GetDomainCatParentCatResp1Msg();
//    				getDomainCatParentCatResp1Msg.setCategoryName("Retailer");
//    				String catSelected=getDomainCatParentCatResp1Msg.getCategoryName();
//    				if(logdUserCategoryName.equalsIgnoreCase("Super Distributor")) {
//            			for(int j=0;j<parentCatRule.indexOf(catSelected);j=j+2) {
//            				users=new ArrayList();
//            				getDomainCatParentCatResp2Msg= new GetDomainCatParentCatResp2Msg();
//            				getDomainCatParentCatResp2Msg.setParentCategory(parentCatRule.get(j));
//            				getDomainCatParentCatResp2Msg.setParentCategoryCode(parentCatRule.get(j+1));
//            				String parentCat=getDomainCatParentCatResp2Msg.getParentCategory();
//            				if(logdUserCategoryCode.equalsIgnoreCase(parentCat)){
//            	        		GetDomainCatParentCatParentUserMsg getDomainCatParentCatParentUserMsg=new GetDomainCatParentCatParentUserMsg();
//            	        		getDomainCatParentCatParentUserMsg.setParentUserName(channelUserVO.getUserName());
//            	        		users.add(getDomainCatParentCatParentUserMsg);
//            	        			
//            	        		
//            	        	}else {
//            	        		String userSelectedParentCatCode=userDao.getCategoryNameFromCatCode(con, null, parentCat);
//            	        		
//            	        		users=userDao.getUsersInHierachyWithCat(con, userSelectedParentCatCode, channelUserVO.getUserID(),"%");
//            	        		
//            	        	}
//            				
//            				getDomainCatParentCatResp2Msg.setParentUser(users);
//            				resp2.add(getDomainCatParentCatResp2Msg);
//            			}
//            		
//            		}else if(!logdUserCategoryName.equalsIgnoreCase("Super Distributor")){
//            			for(int j=2;j<parentCatRule.indexOf(catSelected);j=j+2) {
//            				users=new ArrayList();
//            				getDomainCatParentCatResp2Msg= new GetDomainCatParentCatResp2Msg();
//            				getDomainCatParentCatResp2Msg.setParentCategory(parentCatRule.get(j));
//            				getDomainCatParentCatResp2Msg.setParentCategoryCode(parentCatRule.get(j+1));
//            				String parentCat=getDomainCatParentCatResp2Msg.getParentCategory();
//            				if(logdUserCategoryCode.equalsIgnoreCase(parentCat)){
//            	        		GetDomainCatParentCatParentUserMsg getDomainCatParentCatParentUserMsg=new GetDomainCatParentCatParentUserMsg();
//            	        		getDomainCatParentCatParentUserMsg.setParentUserName(channelUserVO.getUserName());
//            	        		users.add(getDomainCatParentCatParentUserMsg);
//            	        			
//            	        		
//            	        	}else {
//            	        		String userSelectedParentCatCode=userDao.getCategoryNameFromCatCode(con, null, parentCat);
//            	        		
//            	        		users=userDao.getUsersInHierachyWithCat(con, userSelectedParentCatCode, channelUserVO.getUserID(),"%");
//            	        		
//            	        	}
//            				
//            				getDomainCatParentCatResp2Msg.setParentUser(users);
//            				resp2.add(getDomainCatParentCatResp2Msg);
//            				
//            			}
//            		}
//    				getDomainCatParentCatResp1Msg.setParentList(resp2);
//    				resp1.add(getDomainCatParentCatResp1Msg);
//    				
//    			}
//        		
//        		
//        	}
	          
        	else {
        		//category name not found
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_FAIL);
				response.setMessage("Enquiry is not successfull");
				return response;
				
			
        		
        	}
        	response.setData(resp1);
        	
        	response.setLoggedInUserDomainName(domainCodeName);
        	response.setLoggedInUserDomainCode(domainCode);
        	response.setLoggedInUserCatName(logdUserCategoryName);
        	response.setLoggedInUserCatCode(logdUserCategoryCode);
        	
			
			
			if(!BTSLUtil.isNullOrEmptyList(response.getData())) {
				response.setStatus(PretupsI.RESPONSE_SUCCESS);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				 String resmsg  = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
		         response.setMessage(resmsg);

			}else {
				response.setStatus(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(PretupsErrorCodesI.C2S_TRANSFER_FAIL);
				response.setMessage("Enquiry is not successfull");
				return response;
				
			}
			 

			 
			
	 } catch (BTSLBaseException be) {
		 log.error(methodName, "Exception:e=" + be);
	     log.errorTrace(methodName, be);
	     
		    //String  btslMessage = BTSLUtil.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), null);
			String resmsg  = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
		    //response.setStatus(false);
	 	response.setStatus(PretupsI.RESPONSE_FAIL);
		response.setMessageCode(be.getMessage());
		response.setMessage(resmsg);
	} 
	    catch (Exception e) {
	        log.error(methodName, "Exceptin:e=" + e);
	        log.errorTrace(methodName, e);
	        response.setStatus(PretupsI.RESPONSE_FAIL);
			response.setMessageCode("error.general.processing");
			response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");
	    } finally {
	    	try {
	        	if (mcomCon != null) {
					mcomCon.close("deleteCardGroup#" + "ViewCardGroup");
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
    
    public void populateParentCategoryList(UserForm form , ChannelUserVO p_channelUserSessionVO , String[] p_categoryID) throws BTSLBaseException  {
        final String methodName = "populateParentCategoryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
//        //ActionForward forward = null;

        try {
            final UserForm theForm = (UserForm) form;
            ArrayList list = new ArrayList();
            final ChannelUserVO channelUserSessionVO = (ChannelUserVO) p_channelUserSessionVO;
//            final String[] categoryID = theForm.getChannelCategoryCode().split(":");
            final String[] categoryID = p_categoryID;
            /*
             * OrigParentCategory List contains all(Associated C2S Transfer
             * Rules category)
             * FromCategory and ToCategory information like
             * 
             * Dist -> Ret(Disttributor can transfer to retailer and
             * parentAssociationFlag = Y)
             * The above rule state while adding Retailer parent category can be
             * Distributor
             */
            if (theForm.getOrigParentCategoryList() != null /*&& !BTSLUtil.isNullString(theForm.getChannelCategoryCode())*/) {
                CategoryVO categoryVO = null;
                ChannelTransferRuleVO channelTransferRuleVO = null;
                for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
                    categoryVO = (CategoryVO) theForm.getOrigCategoryList().get(i);
                    /*
                     * If Sequence No == 1 means root owner is adding(suppose
                     * Distributor)
                     * at this time pagentCategory and category both will be
                     * same, just add
                     * the categoryVO into the parentCategoryList
                     */
                    if ("1".equals(categoryID[2]) && categoryID[0].equals(categoryVO.getCategoryCode())) {
                        list = new ArrayList();
                        list.add(categoryVO);
                        break;
                    }
                    /*
                     * In Case of channel admin No need to check the sequence
                     * number
                     * In Case of channel user we need to check the sequence
                     * number
                     */
                    if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
                        for (int m = 0, n = theForm.getOrigParentCategoryList().size(); m < n; m++) {
                            channelTransferRuleVO = (ChannelTransferRuleVO) theForm.getOrigParentCategoryList().get(m);
                            /*
                             * Here three checks are checking
                             * Add those category into the list where
                             * a)FormCategory(origPatentList) =
                             * categoryCode(origcategoryList)
                             * b)selectedCategory(categoryID[0] =
                             * ToCategory(origParentCategoryList)
                             * c)selectedCategory(categoryID[0] !=
                             * FromCategory(origParentCategoryList)
                             */
                            if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && categoryID[0].equals(channelTransferRuleVO.getToCategory()) && !categoryID[0]
                                            .equals(channelTransferRuleVO.getFromCategory())) {
                                list.add(categoryVO);
                            }
                        }
                    } else {
                        for (int m = 0, n = theForm.getOrigParentCategoryList().size(); m < n; m++) {
                            channelTransferRuleVO = (ChannelTransferRuleVO) theForm.getOrigParentCategoryList().get(m);
                            /*
                             * Here three checks are checking
                             * Add those category into the list where
                             * a)FormCategory(origPatentList) =
                             * categoryCode(origcategoryList)
                             * b)selectedCategory(categoryID[0] =
                             * ToCategory(origParentCategoryList)
                             * c)selectedCategory(categoryID[0] !=
                             * FromCategory(origParentCategoryList)
                             */

                            if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && categoryID[0].equals(channelTransferRuleVO.getToCategory()) && !categoryID[0]
                                            .equals(channelTransferRuleVO.getFromCategory())) {
                                if (categoryVO.getSequenceNumber() >= channelUserSessionVO.getCategoryVO().getSequenceNumber()) {
                                    list.add(categoryVO);
                                }
                            }
                        }
                    }
                }
                if (list.isEmpty()) {
                    theForm.setParentCategoryList(list);
//                    final BTSLMessages btslMessage = new BTSLMessages("user.selectchannelcategory.msg.notransferruledefined", "SelectCategoryForAdd");
//                    return super.handleMessage(btslMessage, request, mapping);
                    BTSLBaseException be = new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_NOT_DEFINED);
                    throw be;
                }
            }

            theForm.setParentCategoryList(list);

//            forward = mapping.findForward("SelectCategoryForAdd");

        }catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            throw be;
        }
        catch (Exception e) {
            log.errorTrace(methodName, e);
            //throw e;
            throw new BTSLBaseException(e.getMessage());
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    }
    
    public void loadDomainList(UserForm form, ChannelUserVO p_channelUserSession) throws BTSLBaseException {
        final String methodName = "loadDomainList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        //ActionForward forward = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        ArrayList channelUserTypeList = null;
        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            final UserForm theForm = (UserForm) form;
            // flushing the form
            theForm.flush();

//            final ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);
            final ChannelUserVO channelUserSessionVO = (ChannelUserVO) p_channelUserSession;

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            channelUserTypeList = channelUserWebDAO.loadChannelUserTypeList(con);
            theForm.setChannelUserTypeList(channelUserTypeList);
            final CategoryDAO categoryDAO = new CategoryDAO();
            final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
            /*
             * check the type of user
             * If user Domain_Code = "OPT"
             * a)load the domain list that are associated with the user
             * b)load the category list where domain_code != OPT
             * else
             * a)no need to load the domainList get the domain_code and
             * domainName from the session
             * b)load the category list where domain_code = userDomainCode and
             * sequenceNo > logged In User SequenceNo
             */
            if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
                theForm.setSelectDomainList(BTSLUtil.displayDomainList(channelUserSessionVO.getDomainList()));
                if (theForm.getSelectDomainList() != null && theForm.getSelectDomainList().size() > 0) {
                    theForm.setDomainShowFlag(true);
                } else {
                    theForm.setDomainShowFlag(false);
                }
                theForm.setOrigCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));

                // load the categorylist and parentCategortList
//                forward = this.loadCategoryList(mapping, form, request, response); - not in use currently , need to check for opt user
            } else {
                theForm.setDomainCode(channelUserSessionVO.getDomainID());
                theForm.setDomainCodeDesc(channelUserSessionVO.getDomainName());
                theForm.setDomainShowFlag(true);
                /*
                 * Suppose this is the case when Dist add any user at that time
                 * category dropdown
                 * contains those categories whose sequence no is greater than
                 * the sequence no
                 * of the user that are adding the new user
                 * 
                 * The above parentCategoryList contains all categories of the
                 * particular domain
                 * so we filter the parentList on the basis of sequence no
                 */
                /*
                 * //load the parentCategoryList on the basis of domaincode
                 * //theForm.setParentCategoryList(categoryDAO.
                 * loadCategorListByDomainCode
                 * (con,channelUserSessionVO.getDomainID()));
                 * if(theForm.getParentCategoryList()!=null)
                 * {
                 * CategoryVO categoryVO = null;
                 * ArrayList list = new ArrayList();
                 * for(int i=0,j=theForm.getParentCategoryList().size(); i<j ;
                 * i++)
                 * {
                 * categoryVO =
                 * (CategoryVO)theForm.getParentCategoryList().get(i);
                 * if(categoryVO.getSequenceNumber()>channelUserSessionVO.
                 * getCategoryVO().getSequenceNumber())
                 * {
                 * list.add(categoryVO);
                 * }
                 * }
                 * theForm.setCategoryList(list);
                 * }
                 */
                final ArrayList categoryList = categoryWebDAO.loadCategorListByDomainCode(con, channelUserSessionVO.getDomainID());
                theForm.setOrigCategoryList(categoryList);
                if (categoryList != null) {
                    CategoryVO categoryVO = null;
                    final ArrayList list = new ArrayList();
                    for (int i = 0, j = categoryList.size(); i < j; i++) {
                        categoryVO = (CategoryVO) categoryList.get(i);
                        // added by vikas (if in system preferences
                        // PRF_ASSOCIATE_AGENT flag is true then user can modify
                        // his agent only )
                        // if value of flag is false then user can modify all
                        // the user's below in the hierarchy
                        
                        
                        // Not in use currently
                        /*
                        if ("associate".equals(theForm.getRequestType()) && SystemPreferences.PROFILEASSOCIATE_AGENT_PREFERENCES) {
                            if ((categoryVO.getSequenceNumber() == channelUserSessionVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO
                                            .getCategoryType())) {
                                list.add(categoryVO);
                            }

                        }

                        else if ("associateOther".equals(theForm.getRequestType()) && SystemPreferences.PROFILEASSOCIATE_AGENT_PREFERENCES) {
                            if ((categoryVO.getSequenceNumber() == channelUserSessionVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO
                                            .getCategoryType())) {
                                list.add(categoryVO);
                            }

                        } else */
                        
                        if (categoryVO.getSequenceNumber() > channelUserSessionVO.getCategoryVO().getSequenceNumber()) {
                            list.add(categoryVO);
                        }
                    }
                    theForm.setCategoryList(list);
                   /* if ("associate".equals(theForm.getRequestType()) && SystemPreferences.PROFILEASSOCIATE_AGENT_PREFERENCES) {
                        if (list.size() <= 0) {
                            throw new BTSLBaseException(this, methodName, "user.loaddomainlist.error.noagentcategoryfound");
                        }
                    }

                    else if ("associateOther".equals(theForm.getRequestType()) && SystemPreferences.PROFILEASSOCIATE_AGENT_PREFERENCES) {
                        if (list.size() <= 0) {
                            throw new BTSLBaseException(this, methodName, "user.loaddomainlist.error.noagentcategoryfound");
                        }
                    } */

                }
            }

            final C2STransferDAO c2STransferDAO = new C2STransferDAO();
            /*
             * Here we load all transfer rules(from_category and to category),
             * parent category list will populate on the basis of
             * category drop down value by calling a method of the same class
             * populateParentCategoryList
             */
            theForm.setOrigParentCategoryList(c2STransferDAO.loadC2SRulesListForChannelUserAssociation(con, channelUserSessionVO.getNetworkID()));
            theForm.setParentCategoryList(null);

            final ArrayList list = channelUserSessionVO.getGeographicalAreaList();
            /*
             * if list size greater than 1, means user associated with multiple
             * geographies
             * e.g BCU assosciated with multiple zones so first we need to
             * select the zone
             * first, so here we set on the form for user selction
             */
            if (list != null && list.size() > 1) {
                theForm.setAssociatedGeographicalList(list);
                final UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
                theForm.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
            } else if (list != null && list.size() == 1) {
                theForm.setAssociatedGeographicalList(null);
                final UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
                theForm.setParentDomainCode(vo.getGraphDomainCode());
                theForm.setParentDomainDesc(vo.getGraphDomainName());
                theForm.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
            } else {
                theForm.setAssociatedGeographicalList(null);
            }

        } catch (BTSLBaseException e) {
            log.errorTrace(methodName, e);
            throw e;
//            return super.handleError(this, methodName, e, request, mapping);
        }catch (Exception e) {
            log.errorTrace(methodName, e);
            //throw e;
            throw new BTSLBaseException(e.getMessage());
//            return super.handleError(this, methodName, e, request, mapping);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("GetDomainCategoryParentController#"+methodName);
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
    }


    
}

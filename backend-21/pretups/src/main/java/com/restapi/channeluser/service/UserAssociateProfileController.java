package com.restapi.channeluser.service;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

/*@Path("/v1/channelUsers")*/

@io.swagger.v3.oas.annotations.tags.Tag(name = "${UserAssociateProfileController.name}", description = "${UserAssociateProfileController.desc}")//@Api(tags= "Channel Users", value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class UserAssociateProfileController {
	public static final Log log = LogFactory.getLog(UserAssociateProfileController.class.getName());
	/*@GET
    @Path("/associateProfile/{id}")*/
	//@Context
	//private HttpServletResponse response;
	
	@GetMapping(value= "/associateProfile", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    //@Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(tags= "Channel Users", value = "Get Grade list, Commission profile list, Transfer profile list, Transfer rule type list, LMS Profile list",
				  notes = ("Api Info:") + ("\n") + ("Transfer Rule Type list and LMS Profile list will be shown on the basis of System Preference") +("\n") + 
				      ("1. Transfer Rule For User Level") +
				      ("\n") + ("2. LMS APPLICABLE"),
				  authorizations = {
		    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = UserAssociateProfileResponseVO.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${associateProfile.summary}", description="${associateProfile.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserAssociateProfileResponseVO.class))
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

	public UserAssociateProfileResponseVO getProfileLists(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
//			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true,allowableValues = "LOGINID,MSISDN")
//			@RequestParam("identifierType") String identifiertype,
//			 @Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_VALUE, required = true)
//			@RequestParam("identifierValue") String identifiervalue,
			@Parameter(description = "Category Of The Channel User To Search.", required = true)// allowableValues = "Super Distributor,Dealer,Agent,Retailer", required = true)
			@RequestParam("category")String userCategoryName,
			@Parameter(description = "Geography Of The Channel User To Search", required = false)
			@RequestParam("geography") String userGeography,
//			@RequestParam("networkCode") String networkCode ,
			HttpServletResponse response          
			) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "getProfileLists";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
        MComConnectionI mcomCon = null;
        UserAssociateProfileResponseVO response1 = null;
        UserVO sessionUserVO = new UserVO();
        String messageArray[] = new String[1];
        OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		String identifierValue = null;
        try {
        	oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response);
			identifierValue= oAuthUser.getData().getLoginid();
        	identifierValue =  SqlParameterEncoder.encodeParams(identifierValue);
        
        	userCategoryName =  SqlParameterEncoder.encodeParams(userCategoryName);
        	userGeography =  SqlParameterEncoder.encodeParams(userGeography);
        	//networkCode =  SqlParameterEncoder.encodeParams(networkCode);
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response1 = new UserAssociateProfileResponseVO ();
			
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
		
			
//			if (!BTSLUtil.isNullString(networkCode)) {
//                NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
//                if(networkVO==null){
//                messageArray[0] = networkCode;
//               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
//                }
//            }
//			else
//			{
//				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0,null,null);
//			}
//			boolean validateuser = false;
//			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			//validateuser = pretupsRestUtil.validateUser(identifierType, identifierValue, networkCode, con);
//			if(validateuser == false){
//				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0,null,null);
//			}
			final UserDAO userDAO = new UserDAO();
			final UserWebDAO userwebDAO = new UserWebDAO();
			String categoryCode = null;
			String geographyCode = null;
			geographyCode=userGeography;
			categoryCode=userDAO.getCategoryNameFromCatCode(con, null, userCategoryName);
		//	UserVO userVO = new UserVO();
			ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
			
//			 if(PretupsI.MSISDN.equalsIgnoreCase(identifierType)){
//	            	sessionUserVO = (UserVO) userDAO.loadUserDetailsByMsisdn(con,identifierValue);
//	            	if(sessionUserVO==null){
//						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
//					}
//					if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(sessionUserVO.getStatus())||PretupsI.USER_STATUS_DELETED.equalsIgnoreCase(sessionUserVO.getStatus()) ){
//						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
//					}
//	            }
//	            else if(PretupsI.LOGINID.equalsIgnoreCase(identifierType)){
//	            	sessionUserVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(con,identifierValue);
//	            	if(sessionUserVO==null){
//	            		throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
//					}
//	            }
			 
			sessionUserVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(con,identifierValue);
			
			
			CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
			TransferProfileDAO transferProfileDAO = new TransferProfileDAO();
            ArrayList userGradeList = categoryGradeDAO.loadGradeList(con, categoryCode);
            final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, sessionUserVO
                    .getNetworkID(),categoryCode)).booleanValue();
           
            ArrayList transferProfileList = transferProfileDAO.loadTransferProfileByCategoryID(con, sessionUserVO.getNetworkID(), categoryCode, PretupsI.PARENT_PROFILE_ID_USER);
            ArrayList transferRuleTypeList = new ArrayList();
            ArrayList lmsProfileList = new ArrayList<>();
            if(isTrfRuleTypeAllow){
            transferRuleTypeList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true);
            }
            
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
            	lmsProfileList = channelUserWebDAO.getLmsProfileList(con, sessionUserVO.getNetworkID());
            }
            final GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
            // load the geographies info from the user_geographies
           // final ArrayList geographyList = geographyDAO.loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
           
            
           
            ArrayList commissionProfileList = new ArrayList<>();
            
            commissionProfileList = userwebDAO.loadCommisionProfileListByCategoryIDandGeography(con, categoryCode, sessionUserVO
                                    .getNetworkID(), geographyCode);

                
         
            
            HashMap<String, ArrayList<CommissionProfileSetVO> > hMap = new HashMap<String, ArrayList<CommissionProfileSetVO> >();
            
            for(int i=0;i<userGradeList.size();i++){
            	GradeVO gradeVO = (GradeVO)userGradeList.get(i);
            	ArrayList<CommissionProfileSetVO> lst = new ArrayList<CommissionProfileSetVO>();
            	hMap.put(gradeVO.getGradeCode(), lst);
            }
            
            for(int i=0;i<commissionProfileList.size();i++){
            	CommissionProfileSetVO cPSVo = (CommissionProfileSetVO)commissionProfileList.get(i);
            	System.out.println(i);
            	System.out.println(cPSVo.getCommProfileSetName());
            	if(("ALL").equalsIgnoreCase(cPSVo.getGradeCode())){
            		for (Map.Entry<String, ArrayList<CommissionProfileSetVO>> mapElement : hMap.entrySet()){
            			ArrayList<CommissionProfileSetVO> lst =  mapElement.getValue();
            			lst.add(cPSVo);
            		}
            	}
            	else {
            		ArrayList<CommissionProfileSetVO> lst = hMap.get(cPSVo.getGradeCode());
            		if(lst!=null)
            			lst.add(cPSVo);
            	}
            }
            
            List <GradeList> gradeList = Arrays.asList(new GradeList[userGradeList.size()]);
            response1.setGradeList(gradeList);
             for(int i=0;i<userGradeList.size();i++){
             	GradeList grdList =new GradeList();
             	GradeVO gradeVO = new GradeVO();
             	gradeVO = (GradeVO) userGradeList.get(i);
             	grdList.setGradecode(gradeVO.getGradeCode());
             	grdList.setGradeName(gradeVO.getGradeName());
             	ArrayList<CommissionProfileSetVO> commProfileList = hMap.get(gradeVO.getGradeCode());
             	List <CommisionProfileList> comList = Arrays.asList(new CommisionProfileList[commProfileList.size()]);
             	grdList.setCommisionProfileList(comList);
             	for(int j=0;j<commProfileList.size();j++){
             		CommissionProfileSetVO commProfileSetVO = new CommissionProfileSetVO();
             		commProfileSetVO = (CommissionProfileSetVO) commProfileList.get(j);
             		
             			CommisionProfileList cmList = new CommisionProfileList();
             			cmList.setCommprofileCode(commProfileSetVO.getCommProfileSetId());
             			cmList.setCommprofileName(commProfileSetVO.getCommProfileSetName());
             			grdList.getCommisionProfileList().set(j,cmList);
             		}
             	response1.getGradeList().set(i,grdList);
             	}
             
             
             
            List <TransferProfileList> transferProfileList1 = Arrays.asList(new TransferProfileList[transferProfileList.size()]);
            response1.setTransferProfileList(transferProfileList1);
            for(int i=0;i<transferProfileList.size();i++){
            	TransferProfileList trfProfList = new TransferProfileList();
            	ListValueVO listvalueVO = new ListValueVO();
            	listvalueVO = (ListValueVO) transferProfileList.get(i);
            	trfProfList.setTransferprofileCode(listvalueVO.getValue());
            	trfProfList.setTransferprofileName(listvalueVO.getLabel());
            	response1.getTransferProfileList().set(i, trfProfList);
            }
            if(isTrfRuleTypeAllow){
            List <TransferRuleTypeList> transferRuleTypeList1 = Arrays.asList(new TransferRuleTypeList[transferRuleTypeList.size()]);
    		
            response1.setTransferRuleTypeList(transferRuleTypeList1);
            for(int i=0;i<transferRuleTypeList.size();i++){
            	TransferRuleTypeList trfRuleType = new TransferRuleTypeList();
            	ListValueVO listvalueVO = new ListValueVO();
            	listvalueVO = (ListValueVO) transferRuleTypeList.get(i);
            	trfRuleType.setTransferruleCode(listvalueVO.getValue());
            	trfRuleType.setTransferruleName(listvalueVO.getLabel());
            	response1.getTransferRuleTypeList().set(i, trfRuleType);
            }
            }
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()){
            	List <LMSList> comList = Arrays.asList(new LMSList[lmsProfileList.size()]);
             	response1.setLmsList(comList);
             	for(int j=0;j<lmsProfileList.size();j++){
             		ListValueVO listvalueVO = new ListValueVO();
             		listvalueVO = (ListValueVO) lmsProfileList.get(j);
             			LMSList lmlist = new LMSList();
             			lmlist.setLmsprofileCode(listvalueVO.getValue());
             			lmlist.setLmsprofileName(listvalueVO.getLabel());
             			response1.getLmsList().set(j,lmlist);
             		}
            }
            
            response1.setStatus(PretupsI.RESPONSE_SUCCESS);
            response1.setMessageCode(PretupsErrorCodesI.SUCCESS);
            String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), PretupsErrorCodesI.SUCCESS, null);
            response1.setMessage(resmsg);
        }
        catch (BTSLBaseException be) {
        	 log.error(methodName, "Exception:e=" + be);
             log.errorTrace(methodName, be);
             if(Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage()))
           {
             	 response.setStatus(HttpStatus.SC_UNAUTHORIZED);
             	 response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
             }
              else{
              response.setStatus(HttpStatus.SC_BAD_REQUEST);
              response1.setStatus(HttpStatus.SC_BAD_REQUEST);
              }
     	   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), messageArray);
     	    response1.setMessageCode(be.getMessage());
     	   response1.setMessage(resmsg);
     	   
	}
        catch (Exception e) {
        	 response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	response1.setStatus(HttpStatus.SC_BAD_REQUEST);
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
         	  response1.setStatus(PretupsI.RESPONSE_FAIL);
         	  
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("UserAssociateProfileController#getProfileLists");
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


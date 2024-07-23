package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import com.btsl.pretups.domain.businesslogic.DomainDAO;
import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.owasp.esapi.User;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;
import com.btsl.pretups.channel.profile.businesslogic.CategoryTypeEnum;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.OperationTypeEnum;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.ProfileThresholdResponseVO;
import com.btsl.pretups.channel.profile.businesslogic.SearchUserRequestVO;
import com.btsl.pretups.channel.profile.businesslogic.SearchUserResponseVO;
import com.btsl.pretups.channel.profile.businesslogic.SelfCommEnquiryRequestVO;
import com.btsl.pretups.channel.profile.businesslogic.SelfCommEnquiryResponseVO;
import com.btsl.pretups.channel.profile.businesslogic.SelfProfileThresholdRequestVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.UserPasswordManagementVO;
import com.btsl.pretups.channel.profile.businesslogic.UserPinChangeReqVO;
import com.btsl.pretups.channel.profile.businesslogic.UserProfileThresholdRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
//import com.btsl.pretups.channel.profile.businesslogic.SelfCommEnquiryVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.ibm.icu.util.Calendar;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebDAO;
import com.web.user.businesslogic.UserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


@io.swagger.v3.oas.annotations.tags.Tag(name = "${ChannelUserServices.name}", description = "${ChannelUserServices.desc}")//@Api(tags="User Services")
@RestController
@RequestMapping(value = "/v1/userServices")
public class ChannelUserServices {
	
	 
	public static final Log log = LogFactory.getLog(ChannelUserServices.class.getName());
	public static final String classname = "ChannelUserServices";
	
//	private static OperatorUtilI _operatorUtil = null;

    static {/*
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PushMessage[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    */}
    
	/**
	 * It will view all the details of the passed  
	 * loginId.
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 *//*
	@POST
    @Path("/viewSelfDetails")
    @Consumes(value=MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(tags="User Services",value = "View Self details", response = PretupsResponse.class,
	                authorizations = {
    	            @Authorization(value = "Authorization")})
	public PretupsResponse<ChannelUserVO> viewSelfDetails(@RequestBody
			@Parameter(description = "{\r\n" + 
					"  \""+PretupsI.RESTlogGEDIN_IDENTIFIER_TYPE+"\": \"ydist\",\r\n" +
					"  \""+PretupsI.RESTlogGEDIN_IDENTIFIER_VALUE+"\": \"1357\",\r\n" +
					"  \"data\": {\r\n" + 
					"    \"loginId\": \"ydist\"\r\n" + 
					"  }\r\n" + 
					"}\r\n" + 
					"")
			String requestData) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "viewSelfDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        PretupsResponse<ChannelUserVO> response = null;
        try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new PretupsResponse<ChannelUserVO>();
			JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
			String[] allowedCategories = {PretupsI.CATEGORY_CODE_DIST};
			PretupsRestUtil.validateLoggedInUser(requestNode, con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			JsonNode dataNode =  requestNode.get("data");
			if(!dataNode.has("loginId") || dataNode.size() == 0){
				 response.setResponse(PretupsI.RESPONSE_FAIL, false , "Invalid Format");
				return response;
			}
			if(BTSLUtil.isNullString(dataNode.get("loginId").textValue()))
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false , "LoginID is empty");
				return response;
			}
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			
            final String status = PretupsBL.userStatusNotIn();
            final String statusUsed = PretupsI.STATUS_NOTIN;
            ChannelUserVO channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, dataNode.get("loginId").textValue(), null, statusUsed, status);
            if(channelUserVO != null)
            	response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, channelUserVO);
            else
            {
            	response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("");
				response.setMessageKey("");
				response.setMessage(dataNode.get("loginId").textValue() + " doesn't exist in your network.");
				return response;
            }
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
        } catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("UserServices#ViewSelfDetails");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	
	*/
	
	
	
    /*@PostMapping("/viewSelfCommEnquiry")
    @ResponseBody*/
    @POST
    @Path("/viewSelfCommEnquiry")
    @Consumes(value=MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(tags="User Services", value = "View Self Commission Enquiry", response = PretupsResponse.class
			*//*authorizations = {
    	            @Authorization(value = "Authorization")}*//*)
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "successful operation", response = PretupsResponse.class),
	        @ApiResponse(code = 500, message = "Invalid Format"),
	        })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${viewSelfCommEnquiry.summary}", description="${viewSelfCommEnquiry.description}",

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

	public PretupsResponse<SelfCommEnquiryResponseVO>viewSelfCommEnquiry(@RequestBody
			@Parameter(description = SwaggerAPIDescriptionI.SELF_COMMISSION_ENQUIRY_DESC)  SelfCommEnquiryRequestVO selfCommEnquiryRequestVO) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "viewSelfCommEnquiry";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        PretupsResponse<SelfCommEnquiryResponseVO> response = null;
        try {
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new PretupsResponse<SelfCommEnquiryResponseVO>();
			String[] allowedCategories = {PretupsI.CATEGORY_CODE_DIST, PretupsI.CATEGORY_CODE_AGENT, PretupsI.CATEGORY_CODE_RETAILER };
			PretupsRestUtil.validateLoggedInUser(selfCommEnquiryRequestVO.getIdentifierType(),selfCommEnquiryRequestVO.getIdentifierValue(), con, response, allowedCategories);
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			String userId = "";
			String categoryCode = "";
			String networkId = "";
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("SELECT USER_ID,CATEGORY_CODE,NETWORK_CODE FROM USERS WHERE LOGIN_ID =?");
			final String sqlSelect = strBuff.toString();
			PreparedStatement pstmtSelect = null;
			ResultSet rs = null;
			try{
	        	pstmtSelect = con.prepareStatement(sqlSelect);
	        	pstmtSelect.setString(1,selfCommEnquiryRequestVO.getIdentifierType());
	        	rs=pstmtSelect.executeQuery();
	        	if(rs.next()){
	        		userId = rs.getString("USER_ID");
	        		categoryCode = rs.getString("CATEGORY_CODE");
	        		networkId = rs.getString("NETWORK_CODE");
	        	}
			}finally{
				if(rs!=null)
					rs.close();
				if(pstmtSelect!=null)
					pstmtSelect.close();
			}
			Date date=new Date();
			final ListSorterUtil sort = new ListSorterUtil();
			SelfCommEnquiryResponseVO selfCommEnquiryVO = new SelfCommEnquiryResponseVO();
			CommissionProfileWebDAO commissionProfileWebDAO=new CommissionProfileWebDAO();
	        CommissionProfileDAO commissionProfileDAO=new CommissionProfileDAO();
	        CommissionProfileSetVO commissionProfileSetVO=null;
            CommissionProfileSetVersionVO commissionProfileSetVersionVO=null;
            commissionProfileSetVO=commissionProfileWebDAO.loadUserCommSetID(con, userId);
	        selfCommEnquiryVO.setServiceAllowed(commissionProfileWebDAO.serviceAllowed(con, categoryCode));
	        selfCommEnquiryVO.setSequenceNo(commissionProfileDAO.loadsequenceNo(con,categoryCode));
	        selfCommEnquiryVO.setCommissionProfileSetVO(commissionProfileSetVO);
	        commissionProfileSetVersionVO=commissionProfileWebDAO.loadCommProfileSetLatestVersionDetails(con, commissionProfileSetVO.getCommProfileSetId(), date);
	        final ArrayList<CommissionProfileProductsVO> productList = commissionProfileDAO.loadCommissionProfileProductsList(con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVersionVO.getCommProfileSetVersion());
	         if (productList != null && !productList.isEmpty()) {
	             CommissionProfileProductsVO commissionProfileProductsVO;
	             CommissionProfileCombinedVO commissionProfileCombinedVO;
	             final ArrayList<CommissionProfileCombinedVO> commissionList = new ArrayList<CommissionProfileCombinedVO>();
	             ArrayList<AdditionalProfileDeatilsVO> commProfileDetailList;
	             int productsLists=productList.size();
	             for (int i = 0, j = productsLists; i < j; i++) {
	                 commissionProfileProductsVO = (CommissionProfileProductsVO) productList.get(i);
	                 commProfileDetailList = commissionProfileWebDAO.loadCommissionProfileDetailList(con, commissionProfileProductsVO.getCommProfileProductID(),networkId);
	                 commissionProfileCombinedVO = new CommissionProfileCombinedVO();
	                 commissionProfileCombinedVO.setCommissionProfileProductVO(commissionProfileProductsVO);
	                 commProfileDetailList = (ArrayList) sort.doSort("startRange", null, commProfileDetailList);
	                 commissionProfileCombinedVO.setSlabsList(commProfileDetailList);
	                 commissionList.add(commissionProfileCombinedVO);
	             }
	             selfCommEnquiryVO.setCommissionList(commissionList);
	         }
	         final ArrayList<OtfProfileCombinedVO> otfProfileList =new ArrayList<OtfProfileCombinedVO>();;
	         ArrayList<OtfProfileVO> otfProfileVOList = commissionProfileDAO.loadOtfProfileVOList(con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVersionVO.getCommProfileSetVersion());
	         if (otfProfileVOList != null && !otfProfileVOList.isEmpty()){
	        	 ArrayList<OTFDetailsVO> slabList = null;
	        	 for(int i =0 ; i<otfProfileVOList.size(); i++){
	        		 OtfProfileCombinedVO otfProfileCombinedVO = new OtfProfileCombinedVO();
	        		 OtfProfileVO otfProfileVO = (OtfProfileVO)otfProfileVOList.get(i);
	        		 slabList = new ArrayList<OTFDetailsVO>();
	        		 slabList = commissionProfileDAO.loadProfileOtfDetails(con,otfProfileVO.getCommProfileOtfID());
	        		 otfProfileVO.setOtfApplicableFrom(BTSLDateUtil.getSystemLocaleDate(otfProfileVO.getOtfApplicableFrom()));
	        		 otfProfileVO.setOtfApplicableTo(BTSLDateUtil.getSystemLocaleDate(otfProfileVO.getOtfApplicableTo()));
	        		 otfProfileCombinedVO.setOtfProfileVO(otfProfileVO);
	        		 otfProfileCombinedVO.setSlabsList(slabList);
	        		 otfProfileList.add(otfProfileCombinedVO);
	        	 }
	        	 selfCommEnquiryVO.setOtfProfileList(otfProfileList);
	         }
	         
	         final List<AdditionalProfileServicesVO> serviceList = commissionProfileWebDAO.loadAdditionalProfileServicesList(con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVersionVO.getCommProfileSetVersion());

	         if (serviceList != null && !serviceList.isEmpty()) {
	             AdditionalProfileServicesVO additionalProfileServicesVO;
	             AdditionalProfileCombinedVO additionalProfileCombinedVO;
	             AdditionalProfileDeatilsVO additionalProfileDeatilsVO;
	             final ArrayList<AdditionalProfileCombinedVO> additionalList = new ArrayList<AdditionalProfileCombinedVO>();
	             ArrayList<AdditionalProfileDeatilsVO> addProfileDetailList;
	             List<OTFDetailsVO> otfDetailList;
	             AdditionalProfileDeatilsVO aprdvo;
	             int serviceLists=serviceList.size();
	             for (int i = 0, j = serviceLists; i < j; i++) {
	                 additionalProfileServicesVO =serviceList.get(i);

	                 // load Additional Commission Profile Details
	                 addProfileDetailList = commissionProfileWebDAO.loadAdditionalProfileDetailList(con, additionalProfileServicesVO.getCommProfileServiceTypeID(),networkId);
	                 if (!addProfileDetailList.isEmpty()) {
	                     
	                     final List<ListValueVO> finalSelectorList = new ArrayList<ListValueVO>();
	                     final ServiceSelectorMappingCache srvcSelectorMappingCache = new ServiceSelectorMappingCache();
	                     final List selectorList = srvcSelectorMappingCache.getSelectorListForServiceType(additionalProfileServicesVO.getServiceType());
	                     ServiceSelectorMappingVO ssmVO;
	                     int selectorsLists=selectorList.size();
	                     for (int k = 0; k < selectorsLists; k++) {
	                         ssmVO = (ServiceSelectorMappingVO) selectorList.get(k);
	                         final ListValueVO listVO = new ListValueVO(ssmVO.getSelectorName(), ssmVO.getSelectorCode());
	                         finalSelectorList.add(listVO);
	                     }
	                     ListValueVO selectorVO;
	                     if (!BTSLUtil.isNullString(additionalProfileServicesVO.getSubServiceCode())) {
	                         selectorVO = BTSLUtil.getOptionDesc(additionalProfileServicesVO.getSubServiceCode(), finalSelectorList);
	                         additionalProfileServicesVO.setSubServiceDesc(selectorVO.getLabel());
	                     }
	                     additionalProfileCombinedVO = new AdditionalProfileCombinedVO();
	                     additionalProfileCombinedVO.setAdditionalProfileServicesVO(additionalProfileServicesVO);
	                     // sort the list by startrange
	                     if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,networkId)){
	                     int addProfilesDetailLists=addProfileDetailList.size();
	                    	 for(int k =0;k<addProfilesDetailLists;k++){
	                     	aprdvo = (AdditionalProfileDeatilsVO)addProfileDetailList.get(k);
	                     	addProfileDetailList.remove(k);
	                     	otfDetailList = commissionProfileWebDAO.loadProfileOtfDetailList(con, aprdvo.getAddCommProfileDetailID(),aprdvo.getOtfType(),PretupsI.COMM_TYPE_ADNLCOMM);
	                     	aprdvo.setOtfDetails(otfDetailList);
	                     	aprdvo.setOtfDetailsSize(otfDetailList.size());
	                     	addProfileDetailList.add(k,aprdvo);
	                     	
	                     }
	                     }
	                     addProfileDetailList = (ArrayList) sort.doSort("startRange", null, addProfileDetailList);
	                     
	                     selfCommEnquiryVO.setAddProfileDetailList(addProfileDetailList);
	                     additionalProfileCombinedVO.setSlabsList(addProfileDetailList);
	                     additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) addProfileDetailList.get(0);
	                     additionalProfileServicesVO.setAddtnlComStatus(additionalProfileDeatilsVO.getAddtnlComStatus());
	                     additionalProfileServicesVO.setAddtnlComStatusName(additionalProfileDeatilsVO.getAddtnlComStatusName());
	                     selfCommEnquiryVO.setAdditionalProfileDeatilsVO(additionalProfileDeatilsVO);
	                     additionalList.add(additionalProfileCombinedVO);
	                     if (!BTSLUtil.isNullString(additionalProfileServicesVO.getSubServiceCode())) {
	                    	 selfCommEnquiryVO.setAdditionalProfileServicesVO(additionalProfileServicesVO);
	                     }
	                 }
	             }
	             selfCommEnquiryVO.setAdditionalList(additionalList);
	         }
	         
	         response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, selfCommEnquiryVO);
	         
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
        } 
        catch (Exception e) {
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
            response.setStatus(false);
         	  response.setStatusCode(PretupsI.RESPONSE_FAIL);
         	  response.setMessageCode("error.general.processing");
         	  response.setSuccessMsg("Due to some technical reasons, your request could not be processed at this time. Please try later");	
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("UserServices#ViewSelfCommissionEnquiry");
    				mcomCon = null;
    			}
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	
	
	
	
    
    @PostMapping(value= "/passwordManagement", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    /*@Produces(MediaType.APPLICATION_JSON)*/
	/*@ApiOperation(tags="User Services", value = "Manage password for user", notes= SwaggerAPIDescriptionI.PASSWORD_MANAGEMENT_DESC,
				response = BaseResponse.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
	 @ApiResponses(value = {
		        @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
		        @ApiResponse(code = 201, message = "Created"),
		        @ApiResponse(code = 400, message = "Bad Request"),
		        @ApiResponse(code = 401, message = "Unauthorized"),
		        @ApiResponse(code = 404, message = "Not Found")
		        })
  	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${passwordManagement.summary}", description="${passwordManagement.description}",

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

	public PretupsResponse<?> manageUserPassword(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@RequestBody  UserPasswordManagementVO userManagementVO , HttpServletResponse response1) 
			throws IOException, BTSLBaseException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		String msg;
		int resetCount;
		ChannelUserDAO channelUserDAO;
		int updateCount = 0;
		Connection con = null;
		MComConnectionI mcomCon = null;
		LoginDAO loginDAO = new LoginDAO();
		UserVO userVO;
		final String METHOD_NAME = "manageUserPassword";
		String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
		String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
		String pinpasEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
		String c2sDefaultPassword = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD);
		Boolean isWebRandomPwdGenerate = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.WEB_RANDOM_PWD_GENERATE);
		
		ChannelUserVO channelUserVO = null;
		UserDAO userDAO;
		boolean isLoginById;
        String tempResertPwd;
		Date currentDate = new Date();
		String loginID = "";
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
        String pwd = "";
		PretupsResponse<?> response = new PretupsResponse<>();
		final String methodName =  "manageUserPassword";
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}	
		
		 
		OperationTypeEnum operationTypeEnum = OperationTypeEnum.getOperation(userManagementVO.getOperationID());
		if(operationTypeEnum ==null)
		{

			msg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.OPERATION_ID, null);
			response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
			response1.setStatus(PretupsI.RESPONSE_FAIL);
			return response;
		}

		if(BTSLUtil.isEmpty(userManagementVO.getChildLoginId()) &&
		   BTSLUtil.isEmpty(userManagementVO.getChildMsisdn()))
		{
			msg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.VALID_LOG_MSISDN, null);
			response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
			response1.setStatus(PretupsI.RESPONSE_FAIL);
			return response;
		}
		
		
		else if(BTSLUtil.isEmpty(userManagementVO.getChildMsisdn()) &&
		   BTSLUtil.isEmpty(userManagementVO.getChildLoginId()) == false)
		{
			isLoginById = true;
		}
		else
		{
			isLoginById = false;
		}		
		if(BTSLUtil.isEmpty(userManagementVO.getRemarks()))
		{

			msg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.REMARKS_REQUIRED, null);
			response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
			response1.setStatus(PretupsI.RESPONSE_FAIL);
			return response;
		}
		
		OperatorUtilI _operatorUtil = null;
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);

            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        try 
        {   
            mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			channelUserDAO = new ChannelUserDAO();
			userDAO = new UserDAO();
		
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			UserVO VO;
			loginID = oAuthUser.getData().getLoginid();

            //loadchanneluserDetails
			if(isLoginById)
			{
				 VO = userDAO.loadAllUserDetailsByLoginID(con,userManagementVO.getChildLoginId());

				if(!VO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE))
	            	channelUserVO = channelUserDAO.loadChnlUserDetailsByLoginID(con, userManagementVO.getChildLoginId());
				else
					channelUserVO = (ChannelUserVO) VO;
	            
	            if(channelUserVO == null)
	            {
					msg = RestAPIStringParser.getMessage(
							new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							PretupsErrorCodesI.USER_APPROVAL, null);
	            	response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
	            	response1.setStatus(PretupsI.RESPONSE_FAIL);
	            	return response;
	            }
	            
	            if(channelUserVO.getStatus().equals(PretupsI.STATUS_NEW)) {
					msg = RestAPIStringParser.getMessage(
							new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							PretupsErrorCodesI.USER_APPROVAL, null);
	            	response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
	            	response1.setStatus(PretupsI.RESPONSE_FAIL);
	            	return response;
	            }
			}
			else
			{
				VO = userDAO.loadUserDetailsCompletelyByMsisdn(con,userManagementVO.getChildMsisdn());
				if(!VO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE))
					channelUserVO = channelUserDAO.loadChannelUserDetails(con, userManagementVO.getChildMsisdn());
				else
					channelUserVO = (ChannelUserVO) VO;

				if(channelUserVO == null)
	            {
					msg = RestAPIStringParser.getMessage(
							new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							PretupsErrorCodesI.SOS_INVALID_MSISDN, null);
	            	response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
	            	response1.setStatus(PretupsI.RESPONSE_FAIL);
	            	return response;
	            }
			}
            userVO = PretupsRestUtil.getUserVOByLoginId(loginID, con);

            //Changing password of logged in user is not allowed
            if(channelUserVO.getUserID().equalsIgnoreCase(userVO.getUserID()))
            {
				msg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.SAME_LOGIN_ID, null);
            	response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
            	response1.setStatus(PretupsI.RESPONSE_FAIL);
            	return response;
            }
            
            
            
            
           if(channelUserDAO.isUserInHierarchy(con, userVO.getUserID(), "loginId", loginID) == false  && !userVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY) &&  !userVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM) )
           {
           	response.setResponse(Integer.parseInt(PretupsErrorCodesI.USER_NOT_IN_HIERARCHY), false,
           			RestAPIStringParser.getMessage(
    						new Locale(defaultLanguage, defaultCountry),
    						PretupsErrorCodesI.USER_NOT_IN_HIERARCHY, null));
           	response1.setStatus(PretupsI.RESPONSE_FAIL);
           	return response;
           }
           
			if(operationTypeEnum.getId() == 1 || operationTypeEnum.getId() == 2)
			{
				
				 	loginDAO = new LoginDAO();
		            currentDate = new Date();
		            if(!userVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY) && !userVO.getCategoryCode().equals(PretupsI.PWD_CAT_CODE_SUADM)) {
		            channelUserVO.setModifiedBy(userVO.getActiveUserID());
		            channelUserVO.setActiveUserID(userVO.getActiveUserID());
		            } else {
		            	channelUserVO.setModifiedBy(channelUserVO.getParentID());
		            	channelUserVO.setActiveUserID(channelUserVO.getParentID());
		            }
		            channelUserVO.setModifiedOn(currentDate);
				
					if (channelUserVO.getInvalidPasswordCount() < ((Integer) PreferenceCache.getControlPreference(PreferenceI.MAX_PASSWORD_BLOCK_COUNT, channelUserVO.getNetworkID(),
			                channelUserVO.getCategoryCode())).intValue()) 
					{
						msg = RestAPIStringParser.getMessage(
								new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
								PretupsErrorCodesI.PASSWORD_NOT_BLOCKED, null);
			                response.setResponse(PretupsI.RESPONSE_SUCCESS, true, msg);
			                response1.setStatus(PretupsI.RESPONSE_FAIL);
			                return response;
					}
					
					else
					{
					     channelUserVO.setInvalidPasswordCount(0);
			             updateCount = loginDAO.updatePasswordCounter(con, channelUserVO);
					}
				
				  if (updateCount > 0) {
		            	mcomCon.finalCommit();
		            	if(operationTypeEnum.getId() == 2 && !"SHA".equalsIgnoreCase(pinpasEnDeCryptionType))
		            	{
		            		sendPassword(channelUserVO, userManagementVO.getOperationID(), loginID,
				            		userManagementVO.getChildMsisdn(),userManagementVO.getChildLoginId(),userManagementVO.getRemarks(), "");
		            	}
					  msg = RestAPIStringParser.getMessage(
							  new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							  PretupsErrorCodesI.PASSWORD_UNBLOCK_SUCCESS, null);
		            	response.setResponse(PretupsI.RESPONSE_SUCCESS, true, msg);
		            	return response;
				  }
				  else
				  {
					  mcomCon.finalRollback();
					  msg = RestAPIStringParser.getMessage(
							  new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							  PretupsErrorCodesI.OPERATION_FAIL, null);
		              response.setResponse(PretupsI.RESPONSE_FAIL, true, msg);
		              response1.setStatus(PretupsI.RESPONSE_FAIL);
		              return response;
				  }
			}
			
			//Send Password
			if(operationTypeEnum.getId() == 3 && !"SHA".equalsIgnoreCase(pinpasEnDeCryptionType))
			{
				sendPassword(channelUserVO, userManagementVO.getOperationID(), loginID,
			            userManagementVO.getChildMsisdn(),userManagementVO.getChildLoginId(),userManagementVO.getRemarks(), "");
				msg = RestAPIStringParser.getMessage(
						new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
						PretupsErrorCodesI.OPERATION_PASS, null);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, msg);
				return response;
			}
			
			//Reset Password
			if(operationTypeEnum.getId() == 4)
			{		
				
				if (isWebRandomPwdGenerate) {
	                tempResertPwd = _operatorUtil.generateRandomPassword();
	            } else {
	                tempResertPwd = c2sDefaultPassword;
	            }            
					
				resetCount = userDAO.changePassword(con, channelUserVO.getUserID(), BTSLUtil.encryptText
							(tempResertPwd), currentDate, channelUserVO.getModifiedBy(), null);	            
					
				if (resetCount > 0) 
				{
					channelUserVO.setInvalidPasswordCount(0);
	                channelUserVO.setPasswordReset("Y");
	                updateCount = loginDAO.updatePasswordCounter(con, channelUserVO);
					channelUserVO.setPassword(BTSLUtil.encryptText(pwd ));	                
				}
				if (resetCount > 0 && updateCount > 0) 
				{
		            mcomCon.finalCommit();
		            sendPassword(channelUserVO, userManagementVO.getOperationID(), loginID,
		            userManagementVO.getChildMsisdn(),userManagementVO.getChildLoginId(),userManagementVO.getRemarks(), tempResertPwd);
					msg = RestAPIStringParser.getMessage(
							new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							PretupsErrorCodesI.PASSWORD_RESET_SUCCESS, null);
		            response.setResponse(PretupsI.RESPONSE_SUCCESS, true,msg);
		            return response;
				} 
				else 
				{
		            mcomCon.finalRollback();
					msg = RestAPIStringParser.getMessage(
							new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
							PretupsErrorCodesI.OPERATION_FAIL, null);
		            response.setResponse(PretupsI.RESPONSE_FAIL, false,msg);
		            response1.setStatus(PretupsI.RESPONSE_FAIL);
		            return response;
		        }
			}
			return response;
           }
           catch (Exception e)
           {
        	   log.error("sendPassword", "Exception:e=" + e);
			   msg = RestAPIStringParser.getMessage(
					   new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					   PretupsErrorCodesI.FETCH_USER_DET_ERROR, null);
        	   response.setResponse(PretupsI.RESPONSE_FAIL, false, msg);
               return response;
           }
           finally {
   			if (mcomCon != null) {
   				mcomCon.close("ChnlUserPassMgmtAction#sendPassword");
   				mcomCon = null;
   			}
           }
           
	}
	
	
	private void sendPassword(ChannelUserVO channelUserVO, int operationTypeEnumValue, String identifierType,
			String msisdn, String loginID, String remarks, String resertPassword) throws SQLException, BTSLBaseException
	{
        final String METHOD_NAME = "sendPassword";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        final String[] arr = new String[2];
        BTSLMessages btslMessage1 = null;
        // Email for pin & password
        String subject = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        final UserDAO userDAO = new UserDAO();
        final UserWebDAO userwebDAO = new UserWebDAO();
        UserPhoneVO userPhoneVO = null;
        UserEventRemarksVO remarksVO = null;
        ArrayList<UserEventRemarksVO> pinPswdRemarksList = null;
        
        String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Boolean isEmailServiceAllow = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW);
        
        mcomCon = new MComConnection();
        con=mcomCon.getConnection();
        
        if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
            userPhoneVO = userDAO.loadUserPhoneVO(con, channelUserVO.getUserID());
        } else {
            userPhoneVO = userDAO.loadUserPhoneVO(con, channelUserVO.getParentID());
        }
        
        Locale locale = null;
        if (userPhoneVO != null) {
            locale = new Locale(userPhoneVO.getPhoneLanguage(), userPhoneVO.getCountry());
        } else {
            locale = new Locale(defaultLanguage, defaultCountry);
        }
        
        if (operationTypeEnumValue ==4) {
            if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
            	arr[0] = resertPassword;
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_RESETPSWD_MSG, arr);
            } else {
                arr[0] = channelUserVO.getUserName();
                arr[1] = resertPassword;
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_RESETPSWD_STAFF, arr);
            }
            // Email for pin & password

            subject = BTSLUtil.getMessage(locale,"channeluser.unblockpassword.msg.resetsuccess");

        } else if (operationTypeEnumValue == 1) {
            if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_UNBLOCKPSWD_MSG, arr);
            } else {
                arr[0] = channelUserVO.getUserName();
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_UNBLOCKPSWD_STAFF, arr);
            }
            // Email for pin & password
            subject = BTSLUtil.getMessage(locale,"channeluser.unblockpassword.msg.unblocksuccess");
            
        } else if (operationTypeEnumValue == 2) {
            if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
                arr[0] = BTSLUtil.decryptText(channelUserVO.getPassword());
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_UNBLOCKSENDPSWD_MSG, arr);
            } else {
                arr[0] = channelUserVO.getUserName();
                arr[1] = BTSLUtil.decryptText(channelUserVO.getPassword());
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_UNBLOCKSENDPSWD_STAFF, arr);
            }
            // Email for pin & password
            subject = BTSLUtil.getMessage(locale,"channeluser.unblockpassword.msg.unblocksuccess");
        
        } else if (operationTypeEnumValue == 3) {	
            if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
                arr[0] = BTSLUtil.decryptText(channelUserVO.getPassword());
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_SENDPSWD_MSG, arr);
            } else {
                arr[0] = channelUserVO.getUserName();
                arr[1] = BTSLUtil.decryptText(channelUserVO.getPassword());
                btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_SENDPSWD_STAFF, arr);
            }

            // Email for pin & password
            subject = BTSLUtil.getMessage(locale,"channeluser.unblockpassword.msg.sendmsg.success");
        }
      
            if (userPhoneVO != null) {
                int insertCount = 0;
                pinPswdRemarksList = new ArrayList<UserEventRemarksVO>();
                remarksVO = new UserEventRemarksVO();
                remarksVO.setCreatedBy(userPhoneVO.getCreatedBy());
                remarksVO.setCreatedOn(new Date());
                if (operationTypeEnumValue == 3) {
                    remarksVO.setEventType(PretupsI.PASSWD_RESEND);
                } else if (operationTypeEnumValue == 4) {
                    remarksVO.setEventType(PretupsI.PASSWD_RESET);
                }
                else if(operationTypeEnumValue == 1)
            		remarksVO.setEventType(PretupsI.PASSWD_UNBLOCK);
            	else if(operationTypeEnumValue == 2)
            		remarksVO.setEventType(PretupsI.PASSWD_UNBLOCK_RESEND);
                
                remarksVO.setMsisdn(channelUserVO.getMsisdn());
                remarksVO.setRemarks(remarks);
                remarksVO.setUserID(channelUserVO.getUserID());
                remarksVO.setUserType(channelUserVO.getUserType());
                remarksVO.setModule(PretupsI.C2S_MODULE);
                pinPswdRemarksList.add(remarksVO);
                insertCount = userwebDAO.insertEventRemark(con, pinPswdRemarksList);
                if (insertCount <= 0) {
                   // con.rollback();
                	mcomCon.finalRollback();
                    log.error("sendPassword", "Error: while inserting into userEventRemarks Table");
                    throw new BTSLBaseException(this, "save", "error.general.processing");
                }
               // con.commit();
                mcomCon.finalCommit();
            }
            
           
            PushMessage pushMessage = null;
            // PushMessage pushMessage=new
            // PushMessage(channelUserVO.getMsisdn(),btslMessage1,null,null,locale,channelUserVO.getNetworkID());
            if (!BTSLUtil.isNullString(channelUserVO.getMsisdn())) {
                pushMessage = new PushMessage(channelUserVO.getMsisdn(), btslMessage1, null, null, locale, channelUserVO.getNetworkID(),
                    "Related SMS will be delivered shortly");
            } else {
                pushMessage = new PushMessage(userPhoneVO.getMsisdn(), btslMessage1, null, null, locale, channelUserVO.getNetworkID(), "Related SMS will be delivered shortly");
            }

            pushMessage.push();
            // Email for pin & password
            if (isEmailServiceAllow && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                final UserVO sessionUserVO = PretupsRestUtil.getUserVOByLoginId(identifierType, con);
                final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage1, locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                    channelUserVO, sessionUserVO);
                emailSendToUser.sendMail();
            }
    }
	
	@POST
	@Path("/selfthreshold")
	/*@PostMapping("/selfthreshold")
	@ResponseBody*/
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(tags="User Services", value = "View self profile threshold", response = PretupsResponse.class
			*//*authorizations = {
    	            @Authorization(value = "Authorization")}*//*)
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${selfthreshold.summary}", description="${selfthreshold.description}",

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

	public PretupsResponse<ProfileThresholdResponseVO> viewSelfProfileThreshold(@RequestBody
			@Parameter(description = SwaggerAPIDescriptionI.SELF_PROFILE_THRSHOLD_DESC)SelfProfileThresholdRequestVO selfProfileThresholdRequestVO) 
	{
		final String methodName =  "viewSelfProfileThreshold";
		Connection con = null;
		String statusUsed = PretupsI.STATUS_NOTIN;
		String status = PretupsBL.userStatusNotIn();
		final String identifierType = selfProfileThresholdRequestVO.getIdentifierType();
		final String identifierValue = selfProfileThresholdRequestVO.getIdentifierValue();
		String[] allowedCategories = {PretupsI.CATEGORY_CODE_DIST, PretupsI.CATEGORY_CODE_AGENT, PretupsI.CATEGORY_CODE_DEALER,
									  PretupsI.CATEGORY_CODE_RETAILER};
		ChannelUserVO channelUserSessionVO = null;
		MComConnectionI mcomCon = null;
		PretupsResponse<ProfileThresholdResponseVO> response = new PretupsResponse<ProfileThresholdResponseVO>();

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		
        try 
        {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			 
	        PretupsRestUtil.validateLoggedInUser(identifierType, identifierValue, con, response, allowedCategories);
			
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			channelUserSessionVO = returnChannelUser(identifierType, identifierValue,response, con, statusUsed, status);
	        
			return getThresholdResponse(channelUserSessionVO, channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().
	       		 getGrphDomainType(), con, response);
        }
        catch (Exception e) {
    		response.setStatus(false);
    	 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			response.setMessageKey(e.getMessage());
			return response;
	}
       
        finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChannelUserServices#viewSelfProfileThreshold");
        		mcomCon=null;
        		}
            if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting");
            }
        }
	}
	
	@POST
	@Path("/userthreshold")
	
	/*@PostMapping("/userthreshold")
    @ResponseBody
	*/
    
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(tags="User Services", value = "View user profile threshold", response = PretupsResponse.class
			*//*authorizations = {
    	            @Authorization(value = "Authorization")}*//*)
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${userthreshold.summary}", description="${userthreshold.description}",

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

	public PretupsResponse<ProfileThresholdResponseVO> viewUserProfileThreshold(@RequestBody
			@Parameter(description = SwaggerAPIDescriptionI.USER_PROFILE_THRSHOLD_DESC,required =true)UserProfileThresholdRequestVO userProfileThresholdRequestVO) throws SQLException, BTSLBaseException 
	{
		final String methodName = "viewUserProfileThreshold";
		Connection con = null;
		String statusUsed = PretupsI.STATUS_NOTIN;
		String status = PretupsBL.userStatusNotIn();
		MComConnectionI mcomCon = null;
		PretupsResponse<ProfileThresholdResponseVO> response = new PretupsResponse<ProfileThresholdResponseVO>();
		
		final String identifierType = userProfileThresholdRequestVO.getIdentifierType();
		final String identifierValue = userProfileThresholdRequestVO.getIdentifierValue();
		String[] allowedCategories = {PretupsI.CATEGORY_CODE_DIST};
		ChannelUserVO channelUserSessionVO = null;
		
        if (log.isDebugEnabled()) {
        	log.debug(methodName, "Entered");
        }
        try 
        {
	        mcomCon = new MComConnection();
	        con=mcomCon.getConnection();
	        
	        PretupsRestUtil.validateLoggedInUser(identifierType, identifierValue, con, response, allowedCategories);
			
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
	        
			channelUserSessionVO = returnChannelUser(identifierType, identifierValue,response, con, statusUsed, status);
			
			if(BTSLUtil.isNullString(userProfileThresholdRequestVO.getMsisdn()) == false)
			{
				return getThresholdResponseByMsisdn(userProfileThresholdRequestVO.getMsisdn(), channelUserSessionVO, con, statusUsed, status, response);
			}
			else if(BTSLUtil.isNullString(userProfileThresholdRequestVO.getLoginId()) == false)
			{
		        return getThresholdResponseByLoginID(userProfileThresholdRequestVO.getLoginId(), channelUserSessionVO, con, statusUsed, status, response);
			}
			else
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.viewthreshold.invalidinput");
				return response;
			}
	}
		catch (Exception e) {
    		response.setStatus(false);
    	 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
			response.setMessageCode(e.getMessage());
			response.setMessageKey(e.getMessage());
			return response;
	}
        finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChannelUserServices#viewUserProfileThreshold");
        		mcomCon=null;
        		}
            if (log.isDebugEnabled()) {
               log.debug(methodName, "Exiting");
            }
        }
	}


	private ChannelUserVO returnChannelUser(String identifierType, String identifierValue, 
			PretupsResponse<?> response, Connection con, String statusUsed, String status) throws BTSLBaseException
	{	
		ChannelUserVO channelUserSessionVO = new ChannelUserVO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		
		channelUserSessionVO = channelUserDAO.loadUsersDetailsByLoginId(con, identifierType, null, statusUsed, status);    
        return channelUserSessionVO;
	}
	
	private PretupsResponse<ProfileThresholdResponseVO> getThresholdResponse(ChannelUserVO channelUserVO, String sessionUserID, String sessionUserDomainType,
			Connection con, PretupsResponse<ProfileThresholdResponseVO> response) 
			throws BTSLBaseException {
		
		ProfileThresholdResponseVO dataObject = new ProfileThresholdResponseVO();
		final String methodName =  "getThresholdResponse";
		UserWebDAO userwebDAO = new UserWebDAO();
		final boolean isDomainFlag;
		
		response.setDataObject(dataObject);
          
          if(channelUserVO!= null)
		{
			if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
				// throw exception no user exist with this Login Id
				log.error(methodName, "Error: User not exist");
				response.setResponse(PretupsI.RESPONSE_FAIL, false,
						"user.selectchanneluserforview.error.userloginidnotexist");
				return response;
			}
			
			isDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(),
					channelUserVO.getCategoryVO().getGrphDomainType(), sessionUserID, sessionUserDomainType);
			
			if (isDomainFlag == true) {
				this.loadUserCounters(response, channelUserVO, con);
			}
			else 
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false,"user.selectchanneluserforview.error.userloginidnotexist");
				return response;
			}
		}
          else 
          {
          	response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforview.error.userloginidnotexist");
          	return response;
          }
          
		return response;
	}

	private PretupsResponse<ProfileThresholdResponseVO> getThresholdResponseByMsisdn(String msisdn, ChannelUserVO channelUserSessionVO, Connection con, String statusUsed, String status,
				PretupsResponse<ProfileThresholdResponseVO> response) throws BTSLBaseException, SQLException
	{
		final String methodName = "getThresholdResponseByMsisdn";
		String userID = null;
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final String filteredMSISDN = PretupsBL.getFilteredMSISDN(msisdn);
        ChannelUserVO channelUserVO = null;
        
        if (BTSLUtil.isValidMSISDN(msisdn)== false)
        {
        	response.setResponse(PretupsI.RESPONSE_FAIL, false, "btsl.msisdn.error.length");
            return response;
        }
        
        final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(msisdn)));
        
        if (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserSessionVO.getNetworkID())) {
            log.error(methodName, "Error: MSISDN Number" + msisdn + " not belongs to " + channelUserSessionVO.getNetworkName() + "network");        
            response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.assignphone.error.msisdnnotinsamenetwork");
            return response;
        }
       
        if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) 
        {
            channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, null, statusUsed, status);
        } 
        else {
             userID = channelUserSessionVO.getUserID();
            // if user's category is Agent then it can see the details
            // of it's parent's chlid.
            if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
                userID = channelUserSessionVO.getParentID();
            }
            channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userID, statusUsed, status);
        }
        if (channelUserVO != null)
        {
                // check to see if the users are at same level or not
                // if they are at the same level then their category
                // code will be same
                if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode())) {
                    // check the user in the same domain or not
                    log.error(methodName, "Error: User are at the same level");
                    response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforview.error.usermsisdnatsamelevel");
                    return response;
         }

         return getThresholdResponse(channelUserVO, channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().
        		 getGrphDomainType(), con, response);
           
        } else {
            // throw exception no user exist with this Mobile No
            log.error(methodName, "Error: User not exist");
            response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforview.error.usermsisdnnotexist");
            return response;
        }
	}
	
	private PretupsResponse<ProfileThresholdResponseVO> getThresholdResponseByLoginID(String loginId,
			ChannelUserVO channelUserSessionVO, Connection con, String statusUsed, String status,
			PretupsResponse<ProfileThresholdResponseVO> response) throws BTSLBaseException, SQLException {
		
		final String methodName = "getThresholdResponseByLoginID";
        if (log.isDebugEnabled()) {
        	log.debug(methodName, "Entered");
        }
        
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        ChannelUserVO channelUserVO = null;      

        /*
         * If operator user pass userId = null
         * but in case of channel user pass userId = session user Id
         * 
         * In case of channel user we need to perform a Connect By Prior
         * becs load only the child user
         */
        if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
            channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, loginId, null, statusUsed, status);
        } else {
                String userID = channelUserSessionVO.getUserID();
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
                    userID = channelUserSessionVO.getParentID();
                }
                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, loginId, userID, statusUsed, status);
        }

        if (channelUserVO != null) {
            if (PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType())) {
                // throw exception no user exist with this Login Id
                log.error(methodName, "Error: User not exist");
                response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforview.error.userloginidnotexist");
                return response;
            }
                if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode())) {
                    // check the user in the same domain or not
                    log.error(methodName, "Error: User are at the same level");
                    response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforview.error.userloginidatsamelevel");
                    return response;
                }
            
         return getThresholdResponse(channelUserVO, channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().
        		 getGrphDomainType(), con, response);
            	
        }
        else 
        {
        // throw exception no user exist with this Login Id
        	log.error(methodName, "Error: User not exist");
        	response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforview.error.userloginidnotexist");
        	return response;
        }    
	}

	private void loadUserCounters(PretupsResponse<ProfileThresholdResponseVO> response, UserVO p_userVO, Connection p_con) 
	{		
		
        final String methodName = "loadUserCounters";
        if (log.isDebugEnabled()) {
        	log.debug(methodName, "Entered");
        }
        
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final UserTransferCountsDAO userTransferCountsDAO = new UserTransferCountsDAO();
        final TransferProfileDAO transferProfileDAO = new TransferProfileDAO();
        try {
        	
            // the method below is used to load the balance of user
            final ArrayList<UserBalancesVO> userBalanceList = channelUserDAO.loadUserBalances(p_con, p_userVO.getNetworkID(), p_userVO.getNetworkID(), p_userVO.getUserID());
           
            // the method below is used to load the current counters of the user
            UserTransferCountsVO userTransferCountsVO = userTransferCountsDAO.loadTransferCounts(p_con, p_userVO.getUserID(), false);
           
            if (userTransferCountsVO == null) {
                userTransferCountsVO = new UserTransferCountsVO();
            }
            
            final Date p_CurrentDate = new Date(System.currentTimeMillis());
            ChannelTransferBL.checkResetCountersAfterPeriodChange(userTransferCountsVO, p_CurrentDate);
            response.getDataObject().setUserTransferCountsVO(userTransferCountsVO);

            // load the profile details of the user
            final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUser(p_con, p_userVO.getUserID());
           
            if (BTSLUtil.isNullString(channelUserVO.getTransferProfileID())) {
            	response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectchanneluserforviewcounters.msg.noprofileassociated");
            	return ;
            }
            // load the profile counters of the user
            final TransferProfileVO transferProfileVO = transferProfileDAO.loadTransferProfile(p_con, channelUserVO.getTransferProfileID(), p_userVO.getNetworkID(), true);
            if (transferProfileVO != null) {
       
                transferProfileVO.setStatus((BTSLUtil.getOptionDesc(transferProfileVO.getStatus(), LookupsCache.loadLookupDropDown(PretupsI.STATUS_TYPE, true)).getLabel()));
                //response.getDataObject().setTransferProfileVO(transferProfileVO);
                // map the balance with product
                if (userBalanceList != null && userBalanceList.size() > 0) {
                    for (int index1 = 0; index1 < transferProfileVO.getProfileProductList().size(); index1++) {
                        for (int index = 0; index < userBalanceList.size(); index++) {
                            if (((UserBalancesVO) userBalanceList.get(index)).getProductCode().equals(
                                            ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).getProductCode())) {
                                ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).setCurrentBalance(PretupsBL
                                                .getDisplayAmount(((UserBalancesVO) userBalanceList.get(index)).getBalance()));
                                break;
                            } else {
                                ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).setCurrentBalance("0");
                            }
                        }
                    }
                } else {
                    for (int index1 = 0; index1 < transferProfileVO.getProfileProductList().size(); index1++) {
                        ((TransferProfileProductVO) transferProfileVO.getProfileProductList().get(index1)).setCurrentBalance("0");
                    }
                }
                response.getDataObject().setTransferProfileVO(transferProfileVO);
            } else {
            	response.setResponse(PretupsI.RESPONSE_FAIL, false, "batchfoc.processuploadedfile.error.trfprfsuspended");
            	return ;
            }
            // SubscriberOutCountFlag keep tracks of either subscriber out count
            // is allowed or not
            final boolean subscriberOutcount = ((Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.SUBSCRIBER_TRANSFER_OUTCOUNT)).booleanValue();
            
            response.getDataObject().setSubscriberOutCountFlag(subscriberOutcount);
            response.getDataObject().setUserVO(p_userVO);
            
            if (PretupsI.YES.equals(p_userVO.getCategoryVO().getUnctrlTransferAllowed())) {
            	response.getDataObject().setUnctrlTransferFlag(true);
            } else {
            	response.getDataObject().setUnctrlTransferFlag(false);
            }
            response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "");
        }
        catch (Exception e) {
            log.errorTrace(methodName, e);
            response.setResponse(PretupsI.RESPONSE_FAIL, false, "");
            return ;
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/searchuser")

	/*@PostMapping("/searchuser")
    @ResponseBody*/
	@Consumes(value=MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(tags="User Services", value = "Get list of user msisdn, login ID and username", response = PretupsResponse.class
			*//*authorizations = {
    	            @Authorization(value = "Authorization")}*//*)
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${searchuser.summary}", description="${searchuser.description}",

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

	public PretupsResponse<List<SearchUserResponseVO>> getUserList(@RequestBody
			@Parameter(description = SwaggerAPIDescriptionI.SEARCH_USER_DESC)SearchUserRequestVO searchUserRequestVO)
	{
		
		String statusUsed = PretupsI.STATUS_NOTIN;
		String status = PretupsBL.userStatusNotIn();
		MComConnectionI mcomCon = null;
		Connection con = null;
		UserVO user;
		
		final String identifierType = searchUserRequestVO.getIdentifierType();
		final String identifierValue = searchUserRequestVO.getIdentifierValue();
		String[] allowedCategories = {PretupsI.CATEGORY_CODE_DIST};
		ChannelUserVO channelUserSessionVO = null;
		List<SearchUserResponseVO> userResponseList = new ArrayList<SearchUserResponseVO>();
		PretupsResponse<List<SearchUserResponseVO>> response = new PretupsResponse<List<SearchUserResponseVO>>();
		
		final String methodName = "getUserList";
        if (log.isDebugEnabled()) {
        	log.debug(methodName, "Entered");
        }
		ArrayList<UserVO> userList = new ArrayList<UserVO>();
		UserWebDAO userwebDAO = new UserWebDAO();

		try 
		 {
			if(searchUserRequestVO.getCategory() < 1 || searchUserRequestVO.getCategory() >3 )
			{
				response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectuser.error.invalidsearchtype");
	            return response;
			}
			mcomCon = new MComConnection();
	        con=mcomCon.getConnection();
	        
			PretupsRestUtil.validateLoggedInUser(identifierType, identifierValue, con, response, allowedCategories);
			
			if (response.hasFormError()) {
				response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				return response;
			}
			
			channelUserSessionVO = returnChannelUser(identifierType, identifierValue,response, con, statusUsed, status);
			
			userList = userwebDAO.loadUsersList(con, channelUserSessionVO.getNetworkID(), 
					 	CategoryTypeEnum.getOperation(searchUserRequestVO.getCategory()).toString(), "%" + 
					 	searchUserRequestVO.getSearchValue() + "%", null, channelUserSessionVO.getUserID(), 
					 	channelUserSessionVO.getUserID(), statusUsed, status);
		 
		
			if (userList == null || userList.size() <= 0) {
	             response.setResponse(PretupsI.RESPONSE_FAIL, false, "user.selectparentuser.error.usernotexist");
	             return response;
	         } 
			 else if (userList.size() == 1) 
			 {
				 SearchUserResponseVO searchUserResponse = new SearchUserResponseVO();
				 user = userList.get(0);
				 
				 if(user != null)
				 {
					 searchUserResponse.setLoginId(user.getLoginID());
					 searchUserResponse.setMsisdn(user.getMsisdn());
					 searchUserResponse.setUserId(user.getUserID());
					 userResponseList.add(searchUserResponse);
				 }
				 response.setDataObject(userResponseList);
				 response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "");
	         } 
			 else if (userList.size() > 1) 
			 {
				userList.forEach(userVO->
				{
					if(userVO!=null)
					{
						SearchUserResponseVO searchUserResponse = new SearchUserResponseVO();
						searchUserResponse.setLoginId(userVO.getLoginID());
						searchUserResponse.setMsisdn(userVO.getMsisdn());
						searchUserResponse.setUserId(userVO.getUserID());
					 
						userResponseList.add(searchUserResponse);
					} 
				});
				response.setDataObject(userResponseList);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "");
			 }
			return response;
		 }
		 catch (Exception e) 
		 {
	    		response.setStatus(false);
	    	 	response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode(e.getMessage());
				response.setMessageKey(e.getMessage());
				return response;
		}
	       
	        finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close("ChannelUserServices#viewSelfProfileThreshold");
	        		mcomCon=null;
	        		}
	            if (log.isDebugEnabled()) {
	               log.debug(methodName, "Exiting");
	            }
	        }
		 
	}
	/*
	@POST
	@Path("/pinManagement")

	@PostMapping("/pinManagement")
    @ResponseBody
    @Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(tags="User Services", defaultValue = "Reset or send PIN for channel users", notes= SwaggerAPIDescriptionI.PIN_MANAGEMENT_DESC,
				response = BaseResponse.class,
				authorizations = {
	    	            @Authorization(value = "Authorization")})
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")})
	public BaseResponse userPinManagement(
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true, allowableValues = "LOGINID, MSISDN")
			@DefaultValue("") @QueryParam("identifierType") String identifierType,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_VALUE, required = true)
			@DefaultValue("") @QueryParam("identifierValue") String identifierValue,
			@Parameter(description = SwaggerAPIDescriptionI.NETWORK_CODE, hidden = false, required = true)
			@QueryParam("networkCode") String networkCode,
			@Parameter(description = SwaggerAPIDescriptionI.RESET_PIN, required = true, allowableValues = "Y, N")
			@DefaultValue("") @QueryParam("resetPin") String resetPin,
			UserPinMgmtVO userPinMgmtVO) throws IOException, SQLException, BTSLBaseException 
	{

		final String methodName = "userPinManagement";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		String tempResetPin = "";
		BaseResponse response = null;
		int resetCount = 0;
		UserDAO userDAO = new UserDAO();
		int updateCount = 0;
		final String[] arr = new String[1];
	    BTSLMessages btslMessage1 = null;
	    String subject = null;
		UserEventRemarksVO remarksVO = null;
		ArrayList<UserEventRemarksVO> pinPswdRemarksList = null;
		ArrayList<String> arguments = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			response = new BaseResponse();
			boolean validateuser = false;
			if (!BTSLUtil.isNullString(networkCode)) {
				NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
				if (networkVO == null) {
					arguments = new ArrayList<String>();
					arguments.add(networkCode);
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
				}
			} else
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0, null,
						null);

			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			validateuser = pretupsRestUtil.validateUser(identifierType, identifierValue, networkCode, con);
			if (validateuser == false) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0, null, null);
			}

			if (BTSLUtil.isEmpty(networkCode) || BTSLUtil.isEmpty(resetPin) || BTSLUtil.isEmpty(identifierType)
					|| BTSLUtil.isEmpty(identifierValue))
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MAND_PARAMS_MISSING, 0, null, null);

			UserVO loggedInUserVO = null;
			if (identifierType.equalsIgnoreCase("loginid"))
				loggedInUserVO = (UserVO) (userDAO.loadAllUserDetailsByLoginID(con, identifierValue));
			else if (identifierType.equalsIgnoreCase("msisdn"))
				loggedInUserVO = (UserVO) (userDAO.loadUserDetailsByMsisdn(con, identifierValue));

			ChannelUserVO childUser = new UserDAO().loadUserDetailsByMsisdn(con, userPinMgmtVO.getMsisdn());

			if (childUser == null) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0, null, null);
			}

			if (loggedInUserVO.getUserID().equals(childUser.getUserID())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LOGGED_IN_USER_PIN_CHANGE, 0, null,
						null);
			}

			boolean isUserInHierachy = channelUserDAO.isUserInHierarchy(con, loggedInUserVO.getUserID(), "loginid",
					childUser.getLoginID());

			if (!isUserInHierachy) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_HIERARCHY, 0, null, null);
			}
			childUser.setUserPhoneVO(userDAO.loadUserPhoneVO(con, childUser.getUserID()));
			Locale locale = null;
	        if (childUser.getUserPhoneVO().getLocale() == null) {
	            locale = new Locale(childUser.getUserPhoneVO().getPhoneLanguage(), childUser.getUserPhoneVO().getCountry());
	        } else {
	            locale = childUser.getUserPhoneVO().getLocale();
	        }
			if (BTSLUtil.isEmpty(userPinMgmtVO.getRemarks())) {
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REMARKS_REQUIRED, 0, null, null);
			}
			if ("Y".equalsIgnoreCase(resetPin)) {
				childUser.setModifiedBy(loggedInUserVO.getUserID());
				childUser.setModifiedOn(new Date());
				// chnlUserPassPinMgmtForm.setResertPin(tempResertPin);
				final UserPhoneVO userPhoneVO = childUser.getUserPhoneVO();

				if (SystemPreferences.C2S_RANDOM_PIN_GENERATE) {
					tempResetPin = _operatorUtil.generateRandomPin();
				} else {
					tempResetPin = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN));
				}
				childUser.setSmsPin(tempResetPin);
				resetCount = channelUserDAO.changePin(con, tempResetPin, childUser);
				if (resetCount > 0) {
					userPhoneVO.setModifiedBy(loggedInUserVO.getUserID());
					userPhoneVO.setModifiedOn(new Date());
					userPhoneVO.setInvalidPinCount(0);
					userPhoneVO.setPinReset("Y");
					updateCount = channelUserDAO.updateSmsPinCounter(con, userPhoneVO);
				}
				if (resetCount > 0 && updateCount > 0) {

					int insertCount = 0;
					pinPswdRemarksList = new ArrayList<UserEventRemarksVO>();
					remarksVO = new UserEventRemarksVO();
					remarksVO.setCreatedBy(loggedInUserVO.getCreatedBy());
					remarksVO.setCreatedOn(new Date());
					remarksVO.setEventType(PretupsI.PIN_RESET);

					remarksVO.setMsisdn(userPinMgmtVO.getMsisdn());
					remarksVO.setRemarks(userPinMgmtVO.getRemarks());
					remarksVO.setUserID(childUser.getUserID());
					remarksVO.setUserType(childUser.getUserType());
					remarksVO.setModule(PretupsI.C2S_MODULE);
					pinPswdRemarksList.add(remarksVO);
					insertCount = new UserWebDAO().insertEventRemark(con, pinPswdRemarksList);
					if (insertCount <= 0) {
						// con.rollback();
						mcomCon.finalRollback();
						log.error("resetPin", "Error: while inserting into userEventRemarks Table");
						throw new BTSLBaseException(this, "save", "error.general.processing");
					}
						subject = "User PIN is reset successfully.";//BTSLUtil.getMessage(locale,"channeluser.unblockpin.msg.resetsuccess");
						arr[0] = tempResetPin;
			            btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_RESETPIN_MSG, arr);
			            mcomCon.finalCommit();

				} else 
				{
					mcomCon.finalRollback();
	                throw new BTSLBaseException(this, "unblockPin", "channeluser.unblockpin.msg.resetunsuccess", "viewsubscriberdetail");
				}
			}
			else 
			{
	            arr[0] = BTSLUtil.decryptText(childUser.getUserPhoneVO().getSmsPin());
	            btslMessage1 = new BTSLMessages(PretupsErrorCodesI.C2SSUBSCRIBER_SENDPIN_MSG, arr);
	            // Email for pin & password
	            subject = "PIN is send successfully.";//BTSLUtil.getMessage(locale,"channeluser.unblockpin.msg.sendmsg.success");
	            if (arr[0] != null) 
	            {
	                	UserWebDAO userwebDAO = new UserWebDAO();
	                    int insertCount = 0;
	                    remarksVO = new UserEventRemarksVO();
	                    pinPswdRemarksList = new ArrayList<UserEventRemarksVO>();
	                    remarksVO.setCreatedBy(loggedInUserVO.getCreatedBy());
	                    remarksVO.setCreatedOn(new Date());
	                    remarksVO.setEventType(PretupsI.PIN_RESEND);
	                 
	                    remarksVO.setMsisdn(userPinMgmtVO.getMsisdn());
	                    remarksVO.setRemarks(userPinMgmtVO.getRemarks());
	                    remarksVO.setUserID(childUser.getUserID());
	                    remarksVO.setUserType(childUser.getUserType());
	                    remarksVO.setModule(PretupsI.C2S_MODULE);
	                    pinPswdRemarksList.add(remarksVO);
	                    insertCount = userwebDAO.insertEventRemark(con, pinPswdRemarksList);
	                    if (insertCount <= 0) {
	                        //con.rollback();
	                    	mcomCon.finalRollback();
	                        log.error("sendPin", "Error: while inserting into userEventRemarks Table");
	                        throw new BTSLBaseException(this, "save", "error.general.processing");
	                    }
	                    mcomCon.finalCommit();
	                } 
	            }
			
			
	        // PushMessage pushMessage=new
	        // PushMessage(channelUserVO.getMsisdn(),btslMessage1,null,null,locale,channelUserVO.getNetworkID());
	        final PushMessage pushMessage = new PushMessage(childUser.getMsisdn(), btslMessage1, null, null, locale, childUser.getNetworkID(),
	            "SMS will be delivered shortly thanking you");
	        pushMessage.push();
	        // Email for pin & password
	        if (isEmailServiceAllow && !BTSLUtil.isNullString(childUser.getEmail()) 
	        		&& !"SHA".equalsIgnoreCase(pinpasEnDeCryptionType)) {
	            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage1, locale, childUser.getNetworkID(), "Email will be delivered shortly",
	            		childUser, loggedInUserVO);
	            emailSendToUser.sendMail();
	        }
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
            String resmsg  = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
            response.setMessage(resmsg);
		} catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			response.setStatus(400);
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(defaultLanguage, defaultCountry), be.getMessage(),
					BTSLUtil.isNullOrEmptyList(arguments) ? null : arguments.toArray(new String[arguments.size()]));
			response.setMessageCode(be.getMessage());
			response.setMessage(resmsg);
		} catch (Exception e) {
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName, e);
			response.setStatus(400);
			response.setMessageCode(e.getMessage());
			response.setMessage(e.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("ChannelUserServices#" + methodName);
					mcomCon = null;
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
		}
		return response;
	}
	
	*/
	
	
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping(value= "/pinChange", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	    @ResponseBody
	    /*@Produces(MediaType.APPLICATION_JSON)*/
		/*@ApiOperation(tags="User Services", value = SwaggerAPIDescriptionI.PIN_CHANGE,
					response = BaseResponse.class,
					authorizations = {
		    	            @Authorization(value = "Authorization")})
		 @ApiResponses(value = {
			        @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
			        @ApiResponse(code = 201, message = "Created"),
			        @ApiResponse(code = 400, message = "Bad Request"),
			        @ApiResponse(code = 401, message = "Unauthorized"),
			        @ApiResponse(code = 404, message = "Not Found")
			        })
	    */

	 @io.swagger.v3.oas.annotations.Operation(summary = "${pinChange.summary}", description="${pinChange.description}",

			 responses = {
					 @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							 @io.swagger.v3.oas.annotations.media.Content(
									 mediaType = "application/json",
									 array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = BaseResponse.class))
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

	 public BaseResponse userPinChange(
				@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
				@RequestBody  UserPinChangeReqVO userPinChangeReqVO , HttpServletResponse response1) 
				throws IOException, BTSLBaseException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		 final String methodName =  "userPinChange";
			
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}	
				
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDAO = null;
		UserPhoneVO phoneVO = null;
		ArrayList phoneList = new ArrayList();
		int updateCount = 0;
		boolean flag = false;
		boolean selfPinChange = false;
		String smsPin = "";
		boolean pinExistance = false;
		boolean pinExistanceAll = false;
		UserPhoneVO userPhoneVO = null;
		Date currentDate = new Date();
		BaseResponse response = new BaseResponse();

		UserEventRemarksVO userRemarksVO = null;
		ArrayList<UserEventRemarksVO> changePinRemarkList = null;
		OAuthUser oAuthUser = null;
		OAuthUserData oAuthUserData = null;
		String loginID = "";
		
        try {
        	
        	 oAuthUser = new OAuthUser();
    		 oAuthUserData =new OAuthUserData();
    			
    		 oAuthUser.setData(oAuthUserData);
    		 OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
    			
    		loginID = oAuthUser.getData().getLoginid();
            final UserWebDAO userwebDAO = new UserWebDAO();
            
            userPinChangeReqVO.setOldPin(AESEncryptionUtil.aesDecryptor(userPinChangeReqVO.getOldPin(), Constants.A_KEY));
            userPinChangeReqVO.setNewPin(AESEncryptionUtil.aesDecryptor(userPinChangeReqVO.getNewPin(), Constants.A_KEY));
            userPinChangeReqVO.setNewPin2(AESEncryptionUtil.aesDecryptor(userPinChangeReqVO.getNewPin2(), Constants.A_KEY));
           
            
            if (BTSLUtil.isEmpty(userPinChangeReqVO.getRemarks())) 
            {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REMARKS_REQUIRED, PretupsI.RESPONSE_FAIL, null);	
    		}
    		if (BTSLUtil.isEmpty(userPinChangeReqVO.getMsisdn())) 
    		{
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REMARKS_REQUIRED, PretupsI.RESPONSE_FAIL, null);
    		}
    		if (BTSLUtil.isEmpty(userPinChangeReqVO.getOldPin())) 
    		{
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OLD_PIN_REQD, PretupsI.RESPONSE_FAIL, null);
    		}
    		if (BTSLUtil.isEmpty(userPinChangeReqVO.getNewPin()) || BTSLUtil.isEmpty(userPinChangeReqVO.getNewPin2())) 
    		{
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NEW_PIN_REQD, PretupsI.RESPONSE_FAIL, null);
    		}
    		if (userPinChangeReqVO.getNewPin().equalsIgnoreCase(userPinChangeReqVO.getOldPin())) 
    		{
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_OLDNEWSAME, PretupsI.RESPONSE_FAIL, null);
    		}
    		if (!userPinChangeReqVO.getNewPin().equalsIgnoreCase(userPinChangeReqVO.getNewPin2())) {
    			
    			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_NEWCONFIRMNOTSAME, PretupsI.RESPONSE_FAIL, null);
    		}
    		
    		final int result = BTSLUtil.isSMSPinValid(userPinChangeReqVO.getNewPin());
            if (result == -1) {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_REPEATED_CHAR, PretupsI.RESPONSE_FAIL, null);
            } else if (result == 1) {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PIN_CONSECUTIVE_CHAR, PretupsI.RESPONSE_FAIL, null);
            }
           
    		mcomCon = new MComConnection();
 	        con=mcomCon.getConnection();
 	        
 	        
 	       userDAO = new UserDAO();
//    	final ChannelUserVO sessionUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			final ChannelUserVO sessionUserVO = userDAO.loadUserDetailsByMsisdn(con, userPinChangeReqVO.getMsisdn());
			ChannelUserVO pinChangeUserVO=new ChannelUserVO();

//		 jira no:PRETUPS-22174 : Channel admin is Not able to change PIN of channel user which is newly created
//		from channel admin if we try to change pin it is trying to get the channel admin details and changing the PIN of channel admin useing the logged in user instead of channel user.

//		if(PretupsI.CATEGORY_TYPE_OPT.equals(sessionUserVO.getDomainID())){
//    		pinChangeUserVO=sessionUserVO;
//    	}else {
//    		pinChangeUserVO= userDAO.loadUserDetailsCompletelyByMsisdn(con, userPinChangeReqVO.getMsisdn());
//    	}
//			pinChangeUserVO= userDAO.loadUserDetailsCompletelyByMsisdn(con, userPinChangeReqVO.getMsisdn());


			if(sessionUserVO != null)
    	{
    		userPhoneVO = userDAO.loadUserPhoneVO(con, sessionUserVO.getUserID());
    		pinChangeUserVO.setUserPhoneVO(userPhoneVO);
			pinChangeUserVO.setUserID(userPhoneVO.getUserId());
    	}

			if(userPhoneVO == null)
    	{
    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN, PretupsI.RESPONSE_FAIL, null);
    	}
    	
    	phoneVO = pinChangeUserVO.getUserPhoneVO();
    	
//    	if(BTSLUtil.decryptText(phoneVO.getSmsPin()).equalsIgnoreCase(userPinChangeReqVO.getOldPin()) == false)
//    	{
//    		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OLD_PIN_INVALID, PretupsI.RESPONSE_FAIL, null);
//    	}
    	
    	if(sessionUserVO.getUserID().equalsIgnoreCase(pinChangeUserVO.getUserID()))
    	{
    		selfPinChange = true;
    	}
    	
    	handlePin(con, userPinChangeReqVO.getOldPin(), pinChangeUserVO, selfPinChange);
    	
    	
    	/* if (!BTSLUtil.isNullString(phoneVO.getMultiBox()) && "Y".equals(phoneVO.getMultiBox())) 
    	 {*/
    		 phoneVO.setModifiedBy(sessionUserVO.getActiveUserID());
             phoneVO.setModifiedOn(currentDate);
             phoneVO.setPinModifiedOn(currentDate);
             phoneVO.setPinModifyFlag(true); 
    	// }
    	 
         //   phoneList = new ArrayList(theForm.getMsisdnList());
          /*  for (int i = 0, j = phoneList.size(); i < j; i++) {
                phoneVO = (UserPhoneVO) phoneList.get(i);
                if (!BTSLUtil.isNullString(phoneVO.getMultiBox()) && "Y".equals(phoneVO.getMultiBox())) {
                    phoneVO.setModifiedBy(sessionUserVO.getActiveUserID());
                    phoneVO.setModifiedOn(currentDate);
                    phoneVO.setPinModifiedOn(currentDate);
                    phoneVO.setPinModifyFlag(true);
                    if ("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber())) {
                        flag = true;
                        smsPin = phoneVO.getConfirmSmsPin();
                    }
                } else {
                    phoneList.remove(i);
                    i--;
                    j--;
                }
            }*/
          
            // pin existence check
            final String modifificationType = PretupsI.USER_PIN_MANAGEMENT;
            final ArrayList msidnlist = new ArrayList();

            // check for pin existence in password_history table if pin exist
            // add all existence pin to an array list and show error one by one
         
                pinExistance = userDAO.checkPasswordHistory(con, modifificationType, phoneVO.getUserId(), phoneVO.getMsisdn(), BTSLUtil.encryptText(userPinChangeReqVO.getNewPin()));
                if (pinExistance) {
                    msidnlist.add(phoneVO.getMsisdn());
                    pinExistanceAll = true;
                }
            
            if (pinExistanceAll) {
                if (msidnlist != null && !msidnlist.isEmpty()) {
                	
                    final String error_msidn[] = {userPinChangeReqVO.getMsisdn()} ;
                    
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NEW_PIN_ALREADY_USED, PretupsI.RESPONSE_FAIL, error_msidn,null);
                  
                }
            }
            // now prepare the list again as password is in encrypted form

               /* if (!BTSLUtil.isNullString(phoneVO.getMultiBox()) && "Y".equals(phoneVO.getMultiBox())) {*/
                    phoneVO.setOldSmsPin(userPinChangeReqVO.getOldPin());
                    phoneVO.setConfirmSmsPin(userPinChangeReqVO.getNewPin());
                    phoneVO.setShowSmsPin(userPinChangeReqVO.getNewPin());
                //}
                
            phoneList.add(phoneVO);
            updateCount = userwebDAO.updateSmsPin(con, phoneList);  
           
            
            if (updateCount > 0) {
            	response.setStatus(HttpStatus.SC_OK);
            	response.setMessageCode(PretupsErrorCodesI.PIN_CHNG_SUCCESS);
            	response.setMessage(
            			RestAPIStringParser.getMessage(
        						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.PIN_CHNG_SUCCESS,
        						null));
            	
                int changePinCount = 0;
                changePinRemarkList = new ArrayList<UserEventRemarksVO>();
                userRemarksVO = new UserEventRemarksVO();
                userRemarksVO.setCreatedBy(sessionUserVO.getCreatedBy());
                userRemarksVO.setCreatedOn(new Date());
                userRemarksVO.setEventType(PretupsI.CHANGE_PIN);
                userRemarksVO.setMsisdn(phoneVO.getMsisdn());
                userRemarksVO.setRemarks(userPinChangeReqVO.getRemarks());
                userRemarksVO.setUserID(phoneVO.getUserId());
                userRemarksVO.setUserType(sessionUserVO.getUserType());
                userRemarksVO.setModule(PretupsI.C2S_MODULE);
                changePinRemarkList.add(userRemarksVO);
                changePinCount = userwebDAO.insertEventRemark(con, changePinRemarkList);
                if (changePinCount <= 0) {
                    con.rollback();
                    log.error("saveDeleteSuspend", "Error: while inserting into userEventRemarks Table");
                    throw new BTSLBaseException(this, "save", "error.general.processing");
                }
                // Addition By Babu Kunwar Ends
            }
            if (con != null) {
                if (updateCount == phoneList.size()) {
                    con.commit();
                    ChannelUserDAO Chuserdoa =new ChannelUserDAO();
                   // final ChannelUserVO channelUserVO = new ChannelUserVO();
                    ChannelUserVO channelUserVO=Chuserdoa.loadChannelUserDetails(con,phoneVO.getMsisdn());
                    if(channelUserVO!=null)
                    channelUserVO.setMsisdnList(phoneList);
                    else
                    {
                    	 channelUserVO = new ChannelUserVO();
                    	 channelUserVO.setMsisdnList(phoneList);
                    }
                    BTSLMessages btslMessage = null;
                    PushMessage pushMessage = null;
                    BTSLMessages sendbtslMessage = null;
                    if (selfPinChange == false) {
                        // push message
                    	//Added for sending the notification language as per user assigned
			        	Locale locale = null;
	                    if(phoneVO!=null){
	                    	locale = new Locale(phoneVO.getPhoneLanguage(),phoneVO.getCountry());
	                    } else {
	                    	locale = new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	                    }
                        // //Email for pin & password
                        String subject = null;
                        EmailSendToUser emailSendToUser = null;
                        final ChannelUserVO tmpChnlUserVO = new ChannelUserVO();
                        BeanUtils.copyProperties(tmpChnlUserVO, channelUserVO);
                        for (int i = 0, j = phoneList.size(); i < j; i++) {
                            phoneVO = (UserPhoneVO) phoneList.get(i);
                            String msisdn = null;
                            if (!PretupsI.NOT_AVAILABLE.equals(phoneVO.getMsisdn()) && !PretupsI.NOT_AVAILABLE_DESC.equals(phoneVO.getMsisdn())) {
                                msisdn = phoneVO.getMsisdn();
                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { phoneVO.getShowSmsPin() });
                            } else {
                                // msisdn=userDAO.loadParentMsisdn(con,phoneVO.getUserId(),phoneVO.getUserPhonesId());
                                msisdn = sessionUserVO.getMsisdn();
                                sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY_STAFF, new String[] { phoneVO.getShowSmsPin(), pinChangeUserVO.getLoginID() });
                            }

                            // Changed for hiding PIN and PWD that are written
                            // in MessageSentLog
                            pushMessage = new PushMessage(msisdn, sendbtslMessage, "", "", locale, sessionUserVO.getNetworkID(), null, PretupsI.SERVICE_TYPE_CHNL_CHANGEPIN);
                            try {
								pushMessage.push();
							} catch (Exception e) {
								log.errorTrace(methodName, e);
							}
                            // Email for pin & password
                            if (SystemPreferences.IS_EMAIL_SERVICE_ALLOW && !BTSLUtil.isNullString(pinChangeUserVO.getEmail())) {
                            	
                                tmpChnlUserVO.setEmail(pinChangeUserVO.getEmail());
                                tmpChnlUserVO.setUserType("CHANNEL");
                                tmpChnlUserVO.setStatus(pinChangeUserVO.getStatus());
                                tmpChnlUserVO.setUserID(pinChangeUserVO.getUserID());
                                tmpChnlUserVO.setModifiedOn(phoneVO.getModifiedOn());
                                tmpChnlUserVO.setMsisdn(phoneVO.getMsisdn());
                                subject = RestAPIStringParser.getMessage(
                						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), PretupsErrorCodesI.EMAIL_SUBJECT_RESET,
                						null);
                                emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, sessionUserVO.getNetworkID(), "Email will be delivered shortly",
                                                tmpChnlUserVO, sessionUserVO);
                                emailSendToUser.sendMail();
                            }
                        }
                        btslMessage = new BTSLMessages("user.changepin.msg.updatesuccess", "success");
                        ChannelUserLog.log("PINCHANGE", channelUserVO, sessionUserVO, true, "Pin modify");
                    }
                    if (selfPinChange == true) {
                        if (flag) {
                            // modifed by vikram if staff user then set active
                            // user pin
                            if (sessionUserVO.isStaffUser()) {
                                sessionUserVO.setActiveUserPin(BTSLUtil.encryptText(smsPin));
                            } else {
                                sessionUserVO.setSmsPin(BTSLUtil.encryptText(smsPin));
                            }
                        }
                        btslMessage = new BTSLMessages("user.changepin.msg.updatesuccess", "successselfpin");
                        ChannelUserLog.log("SELFPINCHANGE", channelUserVO, sessionUserVO, true, "Self pin modify");
                    }
                } else {
                    con.rollback();
                }
            }
        }
        catch (BTSLBaseException be) {
			log.error(methodName, "Exception:e=" + be);
			log.errorTrace(methodName, be);
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				String unauthorised = Integer.toString(HttpStatus.SC_UNAUTHORIZED);

				response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setMessage(unauthorised);
				response.setMessageCode(unauthorised);
			} else {
				String msg = RestAPIStringParser.getMessage(
						new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessageKey(),
						be.getArgs());
			 	String msgcode=String.valueOf(be.getMessageKey());
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessage(msg);
				response.setMessageCode(msgcode);
				
				

			}

			return response;
		} catch (Exception e) {
			log.debug(methodName, "In catch block");
			log.error(methodName, "Exception:e=" + e);
			log.errorTrace(methodName,  e);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			response.setMessage(e.getMessage());
			response.setMessageCode(e.getMessage());
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			return response;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelUserServices#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting");
			}
		}
		return response;
       
}





	private void handlePin(Connection con, String requestPin, ChannelUserVO senderVO, boolean selfPinChange) throws BTSLBaseException, SQLException {
		String methodName="handlePin";
        int updateStatus = 0;
        boolean pinIsInvalid = false;
        final long pnBlckRstDuration = ((Long) PreferenceCache.getControlPreference(PreferenceI.C2S_PIN_BLK_RST_DURATION, senderVO.getNetworkID(),
        		senderVO.getCategoryCode())).longValue();
        Integer c2sPinMaxLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MAX_LENGTH);
        String pinpasEnDeCryptionType = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE);
        final int maxPinBlckCnt = ((Integer) PreferenceCache.getControlPreference(PreferenceI.C2S_MAX_PIN_BLOCK_COUNT_CODE,
        		senderVO.getNetworkID(), senderVO.getCategoryCode())).intValue();
	    UserPhoneVO userPhoneVO = new UserPhoneVO();
        userPhoneVO = senderVO.getUserPhoneVO();
	    final String decryptedPin = BTSLUtil.decryptText(userPhoneVO.getSmsPin());
  	    BarredUserDAO barredUserDAO = new BarredUserDAO();
  	    
  	    String onMaxInvalidPinPassUserBarredReq = Constants.getProperty("ONMAX_INVALID_PIN_PASSWORD_USER_BARRED_REQUIRED");
  	    boolean isUserBarredRequired = !BTSLUtil.isNullString(onMaxInvalidPinPassUserBarredReq) 
  	    		&& PretupsI.YES.equalsIgnoreCase(onMaxInvalidPinPassUserBarredReq);
  	    
  	  
	    
  	    boolean isBarred= barredUserDAO.isExists(con, "C2S", senderVO.getNetworkID(),userPhoneVO.getMsisdn(),PretupsI.CHANEL_BARRED_USER_TYPE_SENDER,PretupsI.BARRED_TYPE_PIN_INVALID );
	    if(isBarred){
    		userPhoneVO.setBarUserForInvalidPin(true);
    		throw new BTSLBaseException(this, methodName, "c2s.error.pin.blocked", PretupsI.RESPONSE_FAIL, null);
    	}
	    
	    if ("SHA".equalsIgnoreCase(pinpasEnDeCryptionType)) {
	    	if (requestPin.length() > (int)c2sPinMaxLength) {
	    		pinIsInvalid = !decryptedPin.equals(requestPin);
	    		} else {
	    			pinIsInvalid = (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(decryptedPin, requestPin)));
	    			}
	    	} else {
	    		pinIsInvalid = !(decryptedPin.equals(requestPin));
	    		}
	    
	    if (pinIsInvalid) {
	    	final int mintInDay = 24 * 60;
	        if (userPhoneVO.getFirstInvalidPinTime() != null) {
	            // Check if PIN counters needs to be reset after the
	            // reset duration
	            if (log.isDebugEnabled()) {
	            	log.debug(
	                    "UserForm",
	                    "p_senderVO.getModifiedOn().getTime()=" + senderVO.getModifiedOn().getTime() + " p_senderVO.getFirstInvalidPinTime().getTime()=" + userPhoneVO
	                        .getFirstInvalidPinTime().getTime() + " Diff=" + ((senderVO.getModifiedOn().getTime() - userPhoneVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) + " Allowed=" + pnBlckRstDuration);
	            }
	            final Calendar cal = BTSLDateUtil.getInstance();
	            cal.setTime(userPhoneVO.getModifiedOn());
	            final int d1 = cal.get(Calendar.DAY_OF_YEAR);
	            cal.setTime(userPhoneVO.getFirstInvalidPinTime());
	            final int d2 = cal.get(Calendar.DAY_OF_YEAR);
	            if (log.isDebugEnabled()) {
	            	log.debug("UserForm", "Day Of year of Modified On=" + d1 + " Day Of year of FirstInvalidPinTime=" + d2);
	            }
	            if (d1 != d2 && pnBlckRstDuration <= mintInDay) {
                    // reset
                    userPhoneVO.setInvalidPinCount(1);
                    userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                } else if (d1 != d2 && pnBlckRstDuration > mintInDay && (d1 - d2) >= (pnBlckRstDuration / mintInDay)) {
                    // Reset
                    userPhoneVO.setInvalidPinCount(1);
                    userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                } else if (((userPhoneVO.getModifiedOn().getTime() - userPhoneVO.getFirstInvalidPinTime().getTime()) / (60 * 1000)) < pnBlckRstDuration) {
                    if ((userPhoneVO.getInvalidPinCount() - maxPinBlckCnt) == -1 && selfPinChange) {
                    	
                    	userPhoneVO.setInvalidPinCount(0);
                        userPhoneVO.setFirstInvalidPinTime(null);
                        if(isUserBarredRequired)
                        	userPhoneVO.setBarUserForInvalidPin(true);
                        
                    } else {
                        userPhoneVO.setInvalidPinCount(userPhoneVO.getInvalidPinCount() + 1);
                    }

                    if (userPhoneVO.getInvalidPinCount() == 0) {
                        userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                    }
                } else {
                    userPhoneVO.setInvalidPinCount(1);
                    userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
                }
            } else {
                userPhoneVO.setInvalidPinCount(1);
                userPhoneVO.setFirstInvalidPinTime(userPhoneVO.getModifiedOn());
            }
        } else {
            // initilize PIN Counters if ifPinCount>0
            if (userPhoneVO.getInvalidPinCount() > 0) {
                userPhoneVO.setInvalidPinCount(0);
                userPhoneVO.setFirstInvalidPinTime(null);
                updateStatus = new ChannelUserDAO().updateSmsPinCounter(con, userPhoneVO);
                con.commit();
                if (updateStatus < 0) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION, PretupsI.RESPONSE_FAIL, null);
                }
            }
        }
	    
	    
	    if(pinIsInvalid) {
	    	if (userPhoneVO.getInvalidPinCount() == maxPinBlckCnt && selfPinChange) {
		    	userPhoneVO.setInvalidPinCount(0);
	            userPhoneVO.setFirstInvalidPinTime(null);
	            //Handling of Barred User Based on configuration in case of reaching the maximum Invalid PIN count
	            if(isUserBarredRequired)
	            	userPhoneVO.setBarUserForInvalidPin(true);
	            }
	    	
	    	if(userPhoneVO.isBarUserForInvalidPin() && !isBarred){
	    		ChannelUserBL.barSenderMSISDN(con, senderVO, PretupsI.BARRED_TYPE_PIN_INVALID, new Date(), PretupsI.C2S_MODULE);
	    		con.commit();
	    		isBarred = true;
	    		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), 
	        				(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
	    		PushMessage pushMessage = new PushMessage(senderVO.getMsisdn(), 
	    				new BTSLMessages(PretupsErrorCodesI.BARRED_SUBSCRIBER_SYS_RSN), null, null, locale, senderVO.getNetworkCode());
	        	pushMessage.push();
	        }
	    	
	    	updateStatus = new ChannelUserDAO().updateSmsPinCounter(con, userPhoneVO);
            con.commit();
            if (updateStatus > 0) {
            	if(!isBarred)
            		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.OLD_PIN_INVALID, PretupsI.RESPONSE_FAIL, null);
            	if(isBarred)
            		throw new BTSLBaseException(this, methodName, "c2s.error.pin.blocked", PretupsI.RESPONSE_FAIL, null);
            	}
            else {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_EXCEPTION, PretupsI.RESPONSE_FAIL, null);
            }
	    }
	    
	    if (con != null) {
	    	con.commit();
        }
     
	}
}
	
		        

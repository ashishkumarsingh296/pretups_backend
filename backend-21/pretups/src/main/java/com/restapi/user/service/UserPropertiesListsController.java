package com.restapi.user.service;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;

import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

/*@Path("/v1/channelUsers")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${UserPropertiesListsController.name}", description = "${UserPropertiesListsController.desc}")//@Api(tags= "Channel Users", value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")

public class UserPropertiesListsController {
	public static final Log _log = LogFactory.getLog(UserPropertiesListsController.class.getName());
	public static final String QRY_PARAM_NOT_PRESENT = "";
	@SuppressWarnings("unchecked")
	/**
	 * This method populates several lists of diffrent aspects while channel user creation
	 * @param @QueryParam("identifiertype"), @QueryParam("identifiervalue"), @QueryParam("usercategory"), 
	 * @QueryParam("parentCategoryCode"), @QueryParam("parentDomainCode"), @QueryParam("networkCode")
	 * @return  PretupsResponse<HashMap<String, Object> >
	 * @throws IOException, SQLException, BTSLBaseException
	 */
	/*@GET
	@Path("/selectionLists/{userCategory}/{parentCategory}/{parentGeography}")*/
	@GetMapping(value="/selectionLists/{userCategory}/{parentCategory}/{parentGeography}", produces =MediaType.APPLICATION_JSON)
	@ResponseBody
    //@Produces(MediaType.APPLICATION_JSON)
	/*@ApiOperation(tags= "Channel Users", value = "Fetch Miscellaneous Lists for Channel User Creation", response = UserPropertiesListsVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	 @ApiResponses(value = { 
				@ApiResponse(code = 200, message = "OK",response = UserPropertiesListsVO.class),
				@ApiResponse(code = 204, message = "No Content"),
				@ApiResponse(code = 400, message = "Bad Request"),
				@ApiResponse(code = 401, message = "Unauthorized"),
				@ApiResponse(code = 404, message = "Not Found") })
	*/
	@io.swagger.v3.oas.annotations.Operation(summary = "${selectionLists.summary}", description="${selectionLists.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserPropertiesListsVO.class))
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

	public UserPropertiesListsVO userSelectionLists(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
//			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_TYPE, required = true, allowableValues = "LOGINID,MSISDN")
//			@RequestParam("identifierType") String identifiertype,
//			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_VALUE, required = true)
//			@RequestParam("identifierValue") String identifiervalue,
			@Parameter(description = SwaggerAPIDescriptionI.NETWORK_CODE,  required = true)
			@RequestParam("networkCode") String networkCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_CATEGORY, required = true)//allowableValues = "SE,AG,RET")
			@PathVariable("userCategory") String userCategoryCode,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_CATEGORY, required = true)//  allowableValues = "DIST,SE,AG,RET")
			@PathVariable("parentCategory") String parentCategoryCode,
			@Parameter(description = "Parent User ID",required=false)// defaultValue = "",type="Optional")
			@RequestParam("parentUserId") Optional<String> parentUserId1,
			@Parameter(description = SwaggerAPIDescriptionI.CHNL_USER_PARENT_GEOGRAPHY,  required = true)
			@PathVariable("parentGeography") String parentDomainCode,
			HttpServletResponse response1
			) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "userSelectionLists";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        UserPropertiesListsVO response = null;
        ArrayList<String> arguments = null;
        
        OAuthUser oAuthUser= null;
     	OAuthUserData oAuthUserData =null;
     	String identifierValue = null;
     		
        try {
        	
        	oAuthUser = new OAuthUser();
 			oAuthUserData =new OAuthUserData();
 			
 			oAuthUser.setData(oAuthUserData);
 			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
 			identifierValue= oAuthUser.getData().getLoginid();
 			
        	String parentUserId=parentUserId1.map(Object::toString).orElse(null);
			
			mcomCon = new MComConnection();
            con=mcomCon.getConnection();
			response = new UserPropertiesListsVO();
			
			/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
//			OAuthenticationUtil.validateTokenApi(headers);
			
			boolean validateuser = false;
			if (!BTSLUtil.isNullString(networkCode)) {
                NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(networkCode);
                if(networkVO == null){
                	arguments = new ArrayList<String>();
                	arguments.add(networkCode);
                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID);
                }
            }
			else
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0, null, null);
			
//			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
//			validateuser = pretupsRestUtil.validateUser(identifiertype, identifiervalue, networkCode, con);
//			if(validateuser == false){
//				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0, null, null);
//			}		
			
			//if the format of the api is incorrect 
			if(networkCode.equals(QRY_PARAM_NOT_PRESENT) || userCategoryCode.equals(QRY_PARAM_NOT_PRESENT) || parentCategoryCode.equals(QRY_PARAM_NOT_PRESENT) 
					|| parentDomainCode.equals(QRY_PARAM_NOT_PRESENT))
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MAND_PARAMS_MISSING, 0, null, null);
			
			UserVO loggedInUserVO = null;
//			if(identifiertype.equalsIgnoreCase("loginid"))
//				loggedInUserVO = (UserVO) (new UserDAO().loadAllUserDetailsByLoginID(con, identifiervalue));
//			else if(identifiertype.equalsIgnoreCase("msisdn"))
//				loggedInUserVO = (UserVO) (new UserDAO().loadUserDetailsByMsisdn(con, identifiervalue));
			
			loggedInUserVO = (UserVO) (new UserDAO().loadAllUserDetailsByLoginID(con, identifierValue));
			
			ArrayList<ChannelTransferRuleVO> categoriesList = new C2STransferDAO().loadC2SRulesListForChannelUserAssociation(con, networkCode);
			boolean isParentCategoryValid = false, isUserCategoryValid = false, isUserCategoryAllowed = false;
			if(categoriesList != null && categoriesList.size() > 0)
			{
				for(ChannelTransferRuleVO channelTransferRuleVO : categoriesList)
				{
					if(parentCategoryCode.equalsIgnoreCase(channelTransferRuleVO.getFromCategory()))
						isParentCategoryValid = true; 
					if(userCategoryCode.equalsIgnoreCase(channelTransferRuleVO.getToCategory()))
						isUserCategoryValid = true;
				}
			}
			else
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CATEGORYLIST, 0, null, null);
			
			if(!isParentCategoryValid)	
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PARENT_CATEGORY_INVALID, 0, null, null);
			
			if(!isUserCategoryValid)	
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_CATEGORY_INVALID, 0, null, null);
			
			boolean isParentCategoryUnderLoggedUser = loggedInUserVO.getCategoryCode().equalsIgnoreCase(parentCategoryCode)?true:false;
			for(ChannelTransferRuleVO channelTransferRuleVO : categoriesList)
			{
				if(!isParentCategoryUnderLoggedUser)
				{
					if(loggedInUserVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory())
						&& parentCategoryCode.equalsIgnoreCase(channelTransferRuleVO.getToCategory()))
					isParentCategoryUnderLoggedUser = true;
				}
				if(parentCategoryCode.equalsIgnoreCase(channelTransferRuleVO.getFromCategory())
						&& userCategoryCode.equalsIgnoreCase(channelTransferRuleVO.getToCategory()))
					isUserCategoryAllowed = true;
			}
			
			if(!isUserCategoryAllowed)
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_CATEGORY_INVALID_FOR_PARENT_CATEGORY, 0, null, null);
			
			if(!isParentCategoryUnderLoggedUser)
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PARENT_CATEGORY_INVALID_FOR_LOGGED_USER, 0, null, null);
			
			ArrayList<CategoryVO> categoryListByDomain = new CategoryWebDAO().loadCategorListByDomainCode(con, "DIST");
			CategoryVO mainCategoryVO = null;
			for(CategoryVO categoryVO : categoryListByDomain)
			{
				if(categoryVO.getCategoryCode().equals(userCategoryCode))
				{
					mainCategoryVO = categoryVO;
					break;
				}
			}
			ArrayList<UserGeographiesVO> geographyList = new ArrayList<UserGeographiesVO>();
			if (mainCategoryVO.getGrphDomainSequenceNo() == 1) {
                UserGeographiesVO geographyVO = null;
                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(loggedInUserVO.getNetworkID());
                geographyVO.setGraphDomainName(loggedInUserVO.getNetworkName());
                geographyVO.setGraphDomainTypeName(loggedInUserVO.getCategoryVO().getGrphDomainTypeName());
                geographyList.add(geographyVO);
            }
			else if(loggedInUserVO.getCategoryCode().equalsIgnoreCase(parentCategoryCode))
			{
				if (loggedInUserVO.getCategoryVO().getGrphDomainType().equals(mainCategoryVO.getGrphDomainType()))
                    geographyList = loggedInUserVO.getGeographicalAreaList();
				else if((loggedInUserVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == mainCategoryVO.getGrphDomainSequenceNo())
                    geographyList = new GeographicalDomainWebDAO().loadGeographyList(con, networkCode, parentDomainCode, "%");
			}
			else
			{
				if(BTSLUtil.isNullString(parentUserId))
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_PARENT_MISSING, 0, null, null);
				
				UserVO parentUserVO = (UserVO) (new UserDAO().loadUserDetailsFormUserID(con, parentUserId));
				if(parentUserVO == null)
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PARENT_NOT_FOUND , 0, null, null);
				
				else if(!(parentUserVO.getCategoryCode().equalsIgnoreCase(parentCategoryCode)))
				{
					arguments = new ArrayList<String>();
                	arguments.add(parentCategoryCode);
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PARENT_USER_NOT_IN_GIVEN_CATEGORY);
				}

				if(!(parentUserVO.getOwnerID().equalsIgnoreCase(loggedInUserVO.getUserID()) || parentUserVO.getParentID().equalsIgnoreCase(loggedInUserVO.getUserID()))){
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.PARENT_USER_OUTSIDE_LOGGED_USER_HIERARCHY, 0, null, null);
				}
				final ArrayList<UserGeographiesVO> parentUserGeographyList = new GeographicalDomainDAO().loadUserGeographyList(con, parentUserId, networkCode);
				if(parentUserGeographyList == null || parentUserGeographyList.size() == 0)
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PARENT_USER_GEOGRAPHY_NOT_FOUND , 0, null, null);
					
                UserGeographiesVO geographyVO = null;
                if (parentUserGeographyList != null && parentUserGeographyList.size() > 0) {
                    for (int i = 0, j = parentUserGeographyList.size(); i < j; i++) {
                        geographyVO = (UserGeographiesVO) parentUserGeographyList.get(i);
                        if (geographyVO.getGraphDomainCode().equals(parentDomainCode)) {
                            break;
                        }
                    }
                    if (geographyVO.getGraphDomainType().equals(mainCategoryVO.getGrphDomainType()))
                    	geographyList = parentUserGeographyList;

                    else if ((geographyVO.getGraphDomainSequenceNumber() + 1) == mainCategoryVO.getGrphDomainSequenceNo())
                        geographyList = new GeographicalDomainWebDAO().loadGeographyList(con, networkCode, parentDomainCode, "%");
                }
			}
			
			ArrayList<ListValueVO> notificationLanguageList = new LocaleMasterDAO().loadLocaleMasterData();
			ArrayList<ListValueVO> smscProfileList = new UserDAO().loadPhoneProfileList(con, userCategoryCode);
			ArrayList<ListValueVO> documentTypeList = LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true);
			ArrayList<ListValueVO> paymentTypeList = LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
			HashMap<String, ArrayList<UserRolesVO> > groupRolesList = new UserRolesDAO().loadRolesListByGroupRole(con, userCategoryCode, "Y");
			HashMap<String, ArrayList<UserRolesVO> > systemRolesList = new UserRolesDAO().loadRolesListByGroupRole(con, userCategoryCode, "N");
			ArrayList<ListValueVO> servicesList = new ServicesTypeDAO().loadServicesList(con, networkCode, PretupsI.C2S_MODULE, userCategoryCode, false);
			ArrayList<ListValueVO> voucherTypesList = new VomsProductDAO().loadVoucherTypeList(con);
			if (_log.isDebugEnabled()) {
        		_log.debug(methodName, " Lists Loaded ");
        	}
			
			ArrayList<GroupRoleTypeVO> groupRolesList1 = new ArrayList<GroupRoleTypeVO>();
			ArrayList<SystemRoleTypeVO> systemRolesList1 = new ArrayList<SystemRoleTypeVO>();
			ArrayList<ServiceVO> servicesList1 = new ArrayList<ServiceVO>();
			ArrayList<SMSCProfileVO> sMSCProfileList1 = new ArrayList<SMSCProfileVO>();
			ArrayList<DocumentTypeVO> documentTypeList1 = new ArrayList<DocumentTypeVO>();
			ArrayList<PaymentTypeVO> paymentTypeList1 = new ArrayList<PaymentTypeVO>();
			ArrayList<GeographyVO> geographyList1 = new ArrayList<GeographyVO>();
			ArrayList<VoucherTypeVO> voucherTypesList1 = new ArrayList<VoucherTypeVO>();
			ArrayList<LanguageVO> notificationLanguageList1 = new ArrayList<LanguageVO>();
			for(UserGeographiesVO userGeographiesVO : geographyList)
			{
				GeographyVO geographyVO = new GeographyVO();
				geographyVO.setGraphDomainCode(userGeographiesVO.getGraphDomainCode());
				geographyVO.setGraphDomainName(userGeographiesVO.getGraphDomainName());
				geographyVO.setGraphDomainTypeName(userGeographiesVO.getGraphDomainTypeName());
				geographyVO.setParentGraphDomainCode(userGeographiesVO.getParentGraphDomainCode());
				geographyList1.add(geographyVO);
			}
			response.setGeographyList(geographyList1);
			
			for(ListValueVO listValueVO : smscProfileList)
			{
				SMSCProfileVO sMSCProfileVO = new SMSCProfileVO();
				sMSCProfileVO.setSmscProfileCode(listValueVO.getValue());
				sMSCProfileVO.setSmscProfileName(listValueVO.getLabel());
				sMSCProfileList1.add(sMSCProfileVO);
			}
			response.setSMSCProfileList(sMSCProfileList1);
			
			for(ListValueVO listValueVO : documentTypeList)
			{
				DocumentTypeVO documentTypeVO = new DocumentTypeVO();
				documentTypeVO.setDocumentCode(listValueVO.getValue());
				documentTypeVO.setDocumentName(listValueVO.getLabel());
				documentTypeList1.add(documentTypeVO);
			}
			response.setDocumentTypeList(documentTypeList1);
			
			for(ListValueVO listValueVO : paymentTypeList)
			{
				PaymentTypeVO paymentTypeVO = new PaymentTypeVO();
				paymentTypeVO.setPaymentTypeCode(listValueVO.getValue());
				paymentTypeVO.setPaymentTypeName(listValueVO.getLabel());
				paymentTypeList1.add(paymentTypeVO);
			}
			response.setPaymentTypeList(paymentTypeList1);
			
			for (Entry<String, ArrayList<UserRolesVO>> entry : groupRolesList.entrySet()) {
				String groupRoleType = entry.getKey();
				ArrayList<UserRolesVO> groupRoles = entry.getValue();
				GroupRoleTypeVO groupRoleTypeVO = new GroupRoleTypeVO();
				ArrayList<GroupRoleVO> grpRoleList= new ArrayList<GroupRoleVO>();
				groupRoleTypeVO.setGroupRoleType(groupRoleType);
				for(UserRolesVO userRolesVO : groupRoles)
				{
					GroupRoleVO groupRoleVO = new GroupRoleVO();
					groupRoleVO.setGroupName(userRolesVO.getGroupName());
					groupRoleVO.setRoleCode(userRolesVO.getRoleCode());
					groupRoleVO.setRoleName(userRolesVO.getRoleName());
					groupRoleVO.setStatus(userRolesVO.getStatus());
					grpRoleList.add(groupRoleVO);
				}
				groupRoleTypeVO.setGroupRoleList(grpRoleList);
				groupRolesList1.add(groupRoleTypeVO);
			}
			response.setGroupRolesList(groupRolesList1);
			
			for (Entry<String, ArrayList<UserRolesVO>> entry : systemRolesList.entrySet()) {
				String systemRoleType = entry.getKey();
				ArrayList<UserRolesVO> systemRoles = entry.getValue();
				SystemRoleTypeVO systemRoleTypeVO = new SystemRoleTypeVO();
				ArrayList<SystemRoleVO> systemRoleList= new ArrayList<SystemRoleVO>();
				systemRoleTypeVO.setSystemRoleType(systemRoleType);
				for(UserRolesVO userRolesVO : systemRoles)
				{
					SystemRoleVO systemRoleVO = new SystemRoleVO();
					systemRoleVO.setDefaultType(userRolesVO.getDefaultType());
					systemRoleVO.setDomainType(userRolesVO.getDomainType());
					systemRoleVO.setRoleCode(userRolesVO.getRoleCode());
					systemRoleVO.setRoleName(userRolesVO.getRoleName());
					systemRoleVO.setRoleType(userRolesVO.getRoleType());
					systemRoleVO.setStatus(userRolesVO.getStatus());
					systemRoleList.add(systemRoleVO);
				}
				systemRoleTypeVO.setSystemRoleList(systemRoleList);
				systemRolesList1.add(systemRoleTypeVO);
			}
			response.setSystemRolesList(systemRolesList1);
			
			for(ListValueVO listValueVO : servicesList)
			{
				ServiceVO serviceVO = new ServiceVO();
				serviceVO.setServiceCode(listValueVO.getValue());
				serviceVO.setServiceName(listValueVO.getLabel());
				servicesList1.add(serviceVO);
			}
			response.setServicesList(servicesList1);
			
			for(ListValueVO listValueVO : voucherTypesList)
			{
				VoucherTypeVO voucherTypeVO = new VoucherTypeVO();
				voucherTypeVO.setVoucherCode(listValueVO.getValue());
				voucherTypeVO.setVoucherName(listValueVO.getLabel());
				voucherTypesList1.add(voucherTypeVO);
			}
			response.setVoucherTypesList(voucherTypesList1);
			
			for(ListValueVO listValueVO : notificationLanguageList)
			{
				LanguageVO languageVO = new LanguageVO();
				languageVO.setLanguageName(listValueVO.getValue());
				languageVO.setLanguageCode(listValueVO.getLabel());
				notificationLanguageList1.add(languageVO);
			}
			response.setNotificationLanguageList(notificationLanguageList1);
			
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
            response.setMessageCode(PretupsErrorCodesI.SUCCESS);
            String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), PretupsErrorCodesI.SUCCESS, null);
            response.setMessage(resmsg);
        }
        catch (BTSLBaseException be) {
       	 	_log.error(methodName, "Exception:e=" + be);
            _log.errorTrace(methodName, be);
            response.setStatus(400);
            String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), BTSLUtil.isNullOrEmptyList(arguments)?null:arguments.toArray(new String[arguments.size()]));
    	    response.setMessageCode(be.getMessage());
    	    response.setMessage(resmsg);
        }
        catch (Exception e) {
        	_log.error(methodName, "Exception:e=" + e);
            _log.errorTrace(methodName, e);
            response.setStatus(400);
			response.setMessageCode(e.getMessage());
			response.setMessage(e.getMessage());
        } finally {
        	try {
        		if (mcomCon != null) {
        			mcomCon.close("UserPropertiesListsController#userSelectionLists");
        			mcomCon = null;
        		}
        	} 
        	catch (Exception e) {
            _log.errorTrace(methodName, e);
        	}
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, " Exited ");
        	}
        }
        return response;
	}
}

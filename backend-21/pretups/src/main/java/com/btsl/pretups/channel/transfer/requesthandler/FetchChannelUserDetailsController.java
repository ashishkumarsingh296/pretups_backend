package com.btsl.pretups.channel.transfer.requesthandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
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
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.DivisionDeptDAO;
import com.btsl.pretups.master.businesslogic.DivisionDeptVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsDAO;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.AESEncryptionUtil;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserForm;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;



@io.swagger.v3.oas.annotations.tags.Tag(name = "${FetchChannelUserDetailsController.name}", description = "${FetchChannelUserDetailsController.desc}")//@Api(tags="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")

public class FetchChannelUserDetailsController {
	public static final Log log = LogFactory.getLog(FetchChannelUserDetailsController.class.getName());
	
	HashMap systemRolesMap = null;
	HashMap groupRolesMap = null;

	/**
	 * 
	 * @param headers
	 * @param idtype
	 * @param id
	 * @param response1
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	@GetMapping(value="/fetchUserDetails/idValue", produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation( value = "Channel User Details",response = FetchUserDetailsResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")},
	              notes = "Api Info:" + ("\n") + "When User Suspended/Barred, their suspension details will be displayed in \"barredUserDetails\" field.")
	        @ApiResponses(value = { 
			@ApiResponse(code = 200, message = "OK",response = FetchUserDetailsResponseVO.class),
			@ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 404, message = "Not Found") })*/


	@io.swagger.v3.oas.annotations.Operation(summary = "${fetchUserDetails.summary}", description="${fetchUserDetails.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = FetchUserDetailsResponseVO.class))
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



	public FetchUserDetailsResponseVO fetchChannelUserDetails(
			@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			@Parameter(description  = SwaggerAPIDescriptionI.SELECT_MSISDN_OR_LOGINID, required = true)//allowableValues = "LOGINID,MSISDN,EXTCODE")
			@RequestParam("idType") String idtype,
			@Parameter(description = SwaggerAPIDescriptionI.SELECTED_VALUE, example = "",required = true) 
			@RequestParam("idValue") String id,
			HttpServletResponse response1 )
			throws IOException, SQLException, BTSLBaseException {
		
		final String methodName = "fetchChannelUserDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		FetchUserDetailsResponseVO response = new FetchUserDetailsResponseVO();
		UserDAO userDao = new UserDAO();
		PersonalDetailsVO personalDetails = null;
		LoginDetailsVO loginDetails = null;
		PaymentAndServiceDetailsVO paymentAndServiceDetails = new PaymentAndServiceDetailsVO();
		GroupedUserRolesVO userRolesByGroup = new GroupedUserRolesVO();
		ProfileDetailsVO profileDetails = new ProfileDetailsVO();
		OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		 
		try {
			
			response.setService("fetchUserDetails");
			
			// authentication
			oAuthUser = new OAuthUser();
			oAuthUserData =new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser, headers,response1);
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			
			// getting loogedIn user details
			ChannelUserVO sessionUserVO = userDao.loadAllUserDetailsByLoginID( con, oAuthUser.getData().getLoginid() );
			
			UserForm userForm = new UserForm();
			
			//validate request
			this.validateRequestDetails(idtype, id, userForm);
			
			// domain and category list
			this.loadDomainList(con, userForm, sessionUserVO);
			
			personalDetails = new PersonalDetailsVO();
			loginDetails = new LoginDetailsVO();
			paymentAndServiceDetails = new PaymentAndServiceDetailsVO();
			userRolesByGroup = new GroupedUserRolesVO();
            
			// parent search
			this.showParentSearch( con, userForm, sessionUserVO , personalDetails);
			
			// setting modified by username
			if(!BTSLUtil.isNullString(personalDetails.getModifiedByUserId())) {
				ChannelUserVO modifiedByUser = userDao.loadUserDetailsFormUserID(con, personalDetails.getModifiedByUserId());
				if(modifiedByUser != null) {
					personalDetails.setModifiedByUserName(modifiedByUser.getUserName());
				}
			}
	        
			// set user details
			this.setUserDetailsResponse( userForm, personalDetails, loginDetails, paymentAndServiceDetails, profileDetails, userRolesByGroup );

			// user balances
			this.loadUserBalances( con, personalDetails,  userForm.getUserId()); 

			response.setCategoryAuthenticationType(userForm.getCategoryVO().getAuthenticationType());
			// barred user details: if user suspended
			response.setBarredUserDetails( this.loadBarredUserDetails(con, userForm) );

			// user widget
			paymentAndServiceDetails.setUserWidgets( this.loadUserWidget(con, userForm.getUserId()) );
			//oparator user geography
			 if (PretupsI.OPERATOR_USER_TYPE.equalsIgnoreCase(userForm.getUserType())) {
				 loadGeography(con, userForm,response);
				 loadDomainServiceAndVoucherSegment(con,userForm,response);
             }
			//fetching roles(group & subgroup)
			if(userRolesByGroup.getRoleType().equals("N")) {//system role
				HashMap<String,HashMap<String,ArrayList<UserRolesVO>>> rolesMap=null;
				if(PretupsI.OPERATOR_USER_TYPE.equalsIgnoreCase(userForm.getUserType())) {
					 rolesMap = (new UserRolesDAO()).loadRolesListByUserId_ALL(con, userForm.getUserId(), userForm.getCategoryCode(), "N");
				}else {
			     	 rolesMap = (new UserRolesDAO()).loadRolesListByUserId_new(con, userForm.getUserId(), userForm.getCategoryCode(), "N");
				}
				userRolesByGroup.setSystemRolesMap(rolesMap);
			}
			
			
			
			final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
			 ArrayList serviceList =null; 			
				if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(userForm.getCategoryCode())|| PretupsI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userForm.getCategoryCode())) {
					serviceList     = servicesDAO.loadUserServicesList(con, userForm.getUserId());
				}else {
					serviceList     = servicesDAO.loadUserServicesListFrmNetworkServices(con, userForm.getUserId());	
					}
			
			response.setServicesList(serviceList);
			// final response
			response.setPersonalDetails(personalDetails);
			response.setLoginDetails(loginDetails);
			response.setPaymentAndServiceDetails(paymentAndServiceDetails);
			response.setProfileDetails(profileDetails);
			response.setGroupedUserRoles(userRolesByGroup);
			
			

			// final response message
			response.setStatus(PretupsI.RESPONSE_SUCCESS);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String defaultLanguage = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
			String defaultCountry = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
			Locale locale = new Locale(defaultLanguage, defaultCountry);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

			} catch (BTSLBaseException be) {

				log.error(methodName, "Exception:e=" + be);
				log.errorTrace(methodName, be);
				
				if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
					response.setStatus(HttpStatus.SC_UNAUTHORIZED);
					response1.setStatus(HttpStatus.SC_UNAUTHORIZED);
				} else {
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
					response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				}
				
				String resmsg = RestAPIStringParser.getMessage(
						new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))),
						be.getMessageKey(), be.getArgs());
				response.setMessageCode(be.getMessage());
				response.setMessage(resmsg);

			} catch (Exception e) {
				log.error(methodName, "Exceptin:e=" + e);
				log.errorTrace(methodName, e);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setMessageCode(e.toString());
				response.setMessage(e.toString() + " : " + e.getMessage());
			} finally {
				try {
					if (mcomCon != null) {
						mcomCon.close("FetchChannelUserDetailsController#" + "fetchChannelUserDetails");
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
				log.debug(methodName, response);
				log.debug(methodName, "Exiting ");
			}
		}
		return response;
	}
	
	
	/**
	 * 
	 * @param idtype
	 * @param id
	 * @param userForm
	 * @throws BTSLBaseException
	 */
	private void validateRequestDetails(String idtype, String id, UserForm userForm) throws BTSLBaseException {
		final String methodName = "validateRequestDetails";
		if(log.isDebugEnabled()) {
			log.debug(methodName, "Entered idtype: " + idtype + "id: " + id);
		}
		if (BTSLUtil.isNullString(idtype) || BTSLUtil.isNullString(id)) 
		{
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROVIDE_LOGINID_OR_MSISDN, 0, null, null);
		} else if (PretupsI.LOGINID.equalsIgnoreCase(idtype)) 
		{
			userForm.setSearchLoginId(id);
		} else if (PretupsI.MSISDN.equalsIgnoreCase(idtype)) {
			userForm.setSearchMsisdn(id);
		}else if(PretupsI.EXTCODE.equalsIgnoreCase(idtype)){
			userForm.setSearchCriteria(id);
		}
		else {
			final String errorArgs[] = { idtype };
			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_ID_TYPE, errorArgs);
		}
	}
	
	/**
	 * 
	 * @param con
	 * @param theForm
	 * @param channelUserSessionVO
	 * @throws BTSLBaseException
	 */
	private void loadDomainList(Connection con, UserForm theForm, ChannelUserVO channelUserSessionVO) throws BTSLBaseException  {
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
            theForm.setOrigCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));

            // load the categorylist and parentCategortList
            this.loadCategoryList(theForm);
        } else {
        	
            theForm.setDomainCode(channelUserSessionVO.getDomainID());
            theForm.setDomainCodeDesc(channelUserSessionVO.getDomainName());
            
            final ArrayList categoryList = categoryWebDAO.loadCategorListByDomainCode(con, channelUserSessionVO.getDomainID());
            theForm.setOrigCategoryList(categoryList);
            
            if (categoryList != null) {
                CategoryVO categoryVO = null;
                final ArrayList list = new ArrayList();
                for (int i = 0, j = categoryList.size(); i < j; i++) {
                    categoryVO = (CategoryVO) categoryList.get(i);
                }
                theForm.setCategoryList(list);
            }
        }
	}
	
	/**
	 * 
	 * @param form
	 */
    public void loadCategoryList( UserForm form) {
        final String methodName = "loadCategoryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        final UserForm theForm = form;
        final ArrayList list = new ArrayList();
        if (theForm.getOrigCategoryList() != null && !BTSLUtil.isNullString(theForm.getDomainCode())) {
            CategoryVO categoryVO = null;
            for (int i = 0, j = theForm.getOrigCategoryList().size(); i < j; i++) {
                categoryVO = (CategoryVO) theForm.getOrigCategoryList().get(i);
                // here value is the combination of categoryCode,domain_code
                // and sequenceNo so we split the value
                if (categoryVO.getDomainCodeforCategory().equals(theForm.getDomainCode())) {
                    list.add(categoryVO);
                }
            }
        }
        theForm.setCategoryList(list);
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    }

	
	
	
	
	
	
	/**
	 * 
	 * @param con
	 * @param userForm
	 * @param sessionUserVO
	 * @throws BTSLBaseException
	 * @throws Exception
	 */
    public void showParentSearch(Connection con,  UserForm userForm, ChannelUserVO sessionUserVO , PersonalDetailsVO personalDetails) throws BTSLBaseException {
        final String methodName = "showParentSearch";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        UserWebDAO userwebDAO = null;
        ChannelUserWebDAO channelUserWebDAO = null;
        try {
            channelUserWebDAO = new ChannelUserWebDAO();
            userwebDAO = new UserWebDAO();
            final UserForm theForm = userForm;
            DivisionDeptDAO diviDao=new DivisionDeptDAO();  

            final ChannelUserVO channelUserSessionVO = sessionUserVO; // session user: btchadm
            String[] categoryID = null;
            

            String status =  PretupsBL.userStatusNotIn();
            String statusUsed =  PretupsI.STATUS_NOTIN;

            final HashMap<String, String[]> map = new HashMap();
            String[] arr = null;

            if (!BTSLUtil.isNullString(theForm.getSearchMsisdn()))// load User Details by MSISDN
            {
            	
                // check for msisdn belongs to same network or not
                final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(theForm
                                .getSearchMsisdn())));
                if (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserSessionVO.getNetworkID())) {
                    final String[] arr1 = { theForm.getSearchMsisdn(), channelUserSessionVO.getNetworkName() };
                    log.error(methodName, "Error: MSISDN Number" + theForm.getSearchMsisdn() + " not belongs to " + channelUserSessionVO.getNetworkName() + "network");
                    throw new BTSLBaseException(this, methodName, "user.assignphone.error.msisdnnotinsamenetwork", 0, arr1, null);
                }

				/*
				 * mcomCon = new MComConnection(); con=mcomCon.getConnection();
				 */
                final UserDAO userDAO = new UserDAO();

                // load the user info on the basis of msisdn number
                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                final String filteredMSISDN = PretupsBL.getFilteredMSISDN(theForm.getSearchMsisdn());
                ChannelUserVO channelUserVO = null;
                /*
                 * If operator user pass userId = null
                 * but in case of channel user pass userId = session user Id
                 * 
                 * In case of channel user we need to perform a Connect By
                 * Prior becs load only the child user
                 */
                if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
                    channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, null, statusUsed, status);
                } else {
                    String userID = channelUserSessionVO.getUserID();
                    
                    if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
                        userID = channelUserSessionVO.getParentID();
                    }
                    channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userID, statusUsed, status);
                }

                if (channelUserVO != null) {
                    if (TypesI.STAFF_USER_TYPE.equals(channelUserVO.getUserType()) ) {
                        final String arr2[] = { theForm.getSearchMsisdn() };
                        log.error(methodName, "Error: Staff User can not view");
                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.usermsisdnnotexist", 0, arr2, null);
                    }
                    // Added for RSA Authentication
                    boolean rsaRequired = false;
                    rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
                    theForm.setRsaRequired(rsaRequired);
                    // check to see if the users are at same level or not
                    // if they are at the same level then their category code will be same
                    if ( !channelUserSessionVO.getMsisdn().equals(theForm.getSearchMsisdn()) &&  channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) 
                    		&& !PretupsI.USER_TYPE_STAFF.equals(channelUserVO.getUserType())) {
                        // check the user in the same domain or not
                        final String arr2[] = { theForm.getSearchMsisdn() };
                        log.error(methodName, "Error: User are at the same level");
                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.usermsisdnatsamelevel", 0, arr2, null);

                    }
                    
                    // check for searched user is exist in the same domain or not
                    if (theForm.getSelectDomainList() != null) {
                        final boolean isDomainFlag = this.isExistDomain(theForm.getSelectDomainList(), channelUserVO);
                        final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), theForm.getSelectDomainList());
                        theForm.setDomainCodeDesc(listValueVO.getLabel());
                        theForm.setDomainCode(listValueVO.getValue());
                        if (!isDomainFlag) {
                            // check the user in the same domain or not
                            final String arr2[] = { theForm.getSearchMsisdn() };
                            log.error(methodName, "Error: User not in the same domain");
                            throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.usermsisdnnotinsamedomain", 0, arr2, null);
                        }
                    }
                    final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
                                    channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());
                    
                    if (isGeoDomainFlag) {
                        theForm.setCategoryVO(channelUserVO.getCategoryVO());
                        theForm.setCategoryCode(theForm.getCategoryVO().getCategoryCode());
                        theForm.setChannelCategoryCode(theForm.getCategoryVO().getCategoryCode());
                        theForm.setCategoryCodeDesc(theForm.getCategoryVO().getCategoryName());
                        theForm.setChannelCategoryDesc(theForm.getCategoryVO().getCategoryName());
                        theForm.setParentDomainCode(channelUserVO.getParentGeographyCode());
                        //theForm.setParentDomainDesc(channelUserVO.getParentGeographyCode());
                        this.setDetailsOnForm(theForm, con, userDAO, userwebDAO, channelUserVO, sessionUserVO);
                    } else if (!isGeoDomainFlag) {
                        // check the user in the same domain or not
                        final String arr2[] = { theForm.getSearchMsisdn() };
                        log.error(methodName, "Error: User not in the same domain");
                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.usermsisdnnotinsamegeodomain", 0, arr2, null);
                    }

                } else {
                    // throw exception no user exist with this Mobile No
                    final String arr2[] = { theForm.getSearchMsisdn() };
                    log.error(methodName, "Error: User not exist");
                    throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.usermsisdnnotexist", 0, arr2, null);
                }
            } else if (!BTSLUtil.isNullString(theForm.getSearchLoginId()))// load user details by LoginId
            {
				/*
				 * mcomCon = new MComConnection(); con=mcomCon.getConnection();
				 */
                final UserDAO userDAO = new UserDAO();

                // load the user info on the basis of LoginId number
                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                ChannelUserVO channelUserVO = null;
                /*
                 * If operator user pass userId = null
                 * but in case of channel user pass userId = session user Id
                 * 
                 * In case of channel user we need to perform a Connect By
                 * Prior becs load only the child user
                 */
                if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
                    channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, theForm.getSearchLoginId(), null, statusUsed, status);
                } else {
                    String userID = channelUserSessionVO.getUserID();
                    if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
                        userID = channelUserSessionVO.getParentID();
                    }
                
                    channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, theForm.getSearchLoginId(), userID, statusUsed, status);
                }
                if(channelUserVO==null) {
                	// throw exception no user exist with this Login Id
                    final String arr2[] = { theForm.getSearchLoginId() };
                    log.error(methodName, "Error: User not exist");
                    throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotexist", 0, arr2, null);
                }
                theForm.setCategoryCode(channelUserVO.getCategoryCode());
                theForm.setUserType(channelUserVO.getUserType());
                theForm.setCreatedOn(channelUserVO.getCreatedOnAsString());
				theForm.setContactPerson(channelUserVO.getContactPerson());
				theForm.setAuthTypeAllowed(channelUserVO.getAuthTypeAllowed());
                //load created user name
                //division and department name
                if(!BTSLUtil.isNullorEmpty(channelUserVO.getCreatedBy())) {
                	theForm.setCreatedBy(userwebDAO.userNameFromId(con, channelUserVO.getCreatedBy()));
                 }
                
                if(!BTSLUtil.isNullorEmpty(channelUserVO.getDivisionCode())) {
                	theForm.setDivisionCode(channelUserVO.getDivisionCode());
                	DivisionDeptVO division=diviDao.loadDivDepDetailsById(con, channelUserVO.getDivisionCode());
                	if(division!=null) {
                		theForm.setDivisionDesc(division.getDivDeptName());
                	}
                }
                if(!BTSLUtil.isNullorEmpty(channelUserVO.getDepartmentCode())) {
                	theForm.setDepartmentCode(channelUserVO.getDepartmentCode());
                	DivisionDeptVO division=diviDao.loadDivDepDetailsById(con, channelUserVO.getDepartmentCode());
                	if(division!=null) {
                	theForm.setDepartmentDesc(division.getDivDeptName());
                	}
                	
                }
                /*if (channelUserVO != null) {
                    if (PretupsI.OPERATOR_USER_TYPE.equalsIgnoreCase(channelUserVO.getUserType())) {
                        final String arr2[] = { theForm.getSearchLoginId() };
                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotexist", 0, arr2, null);
                    }
                }*/
                // End of add by ved prakash
                
                if (channelUserVO != null) {
                    // Added for RSA Authentication
                    boolean rsaRequired = false;
                    rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
                    theForm.setRsaRequired(rsaRequired);
                    
                    // check to see if the users are at same level or not
                    // if they are at the same level then their category
                    // code will be same
                    if (  !channelUserSessionVO.getLoginID().equals(theForm.getSearchLoginId()) && channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) && 
                    		!PretupsI.USER_TYPE_STAFF.equals(channelUserVO.getUserType())) 
                    {
                        final String arr2[] = { theForm.getSearchLoginId() };
                        log.error(methodName, "Error: User are at the same level");
                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidatsamelevel", 0, arr2, null);
                    }

                    if (theForm.getSelectDomainList() != null) {
                        final boolean isDomainFlag = this.isExistDomain(theForm.getSelectDomainList(), channelUserVO);
                        final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), theForm.getSelectDomainList());
                        theForm.setDomainCodeDesc(listValueVO.getLabel());
                        theForm.setDomainCode(listValueVO.getValue());
//                        if (!isDomainFlag) {
//                            // check the user in the same domain or not
//                            final String arr2[] = { theForm.getSearchLoginId() };
//                            log.error(methodName, "Error: User not in the same domain");
//                            throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotinsamedomain", 0, arr2, null);
//                        }
                        
                    }

                    // check for searched user is exist in the same
                    // geogrphical domain or not
                    final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
                                    channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());

                    if (isGeoDomainFlag||PretupsI.USER_TYPE_OPT.equals(sessionUserVO.getUserType())) {
                        theForm.setCategoryVO(channelUserVO.getCategoryVO());
                        theForm.setCategoryCode(theForm.getCategoryVO().getCategoryCode());
                        theForm.setChannelCategoryCode(theForm.getCategoryVO().getCategoryCode());
                        theForm.setCategoryCodeDesc(theForm.getCategoryVO().getCategoryName());
                        theForm.setChannelCategoryDesc(theForm.getCategoryVO().getCategoryName());
                       // theForm.setParentDomainDesc(channelUserVO.getGeographicalDesc());
                       theForm.setParentDomainCode(channelUserVO.getParentGeographyCode());
                        this.setDetailsOnForm(theForm, con, userDAO, userwebDAO, channelUserVO, sessionUserVO);
                    } else if (!isGeoDomainFlag) {
                        // check the user in the same domain or not
                        final String arr2[] = { theForm.getSearchLoginId() };
                        log.error(methodName, "Error: User not in the same domain");
                        throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotinsamegeodomain", 0, arr2, null);
                    }

                } 
                    
                
                if(channelUserVO != null) {
                	personalDetails.setParentCategoryCode(channelUserVO.getParentCategoryCode());
                	personalDetails.setOwnerCategoryCode(channelUserVO.getOwnerCategoryCode());
                	personalDetails.setModifiedByUserId(channelUserVO.getModifiedBy());
                	personalDetails.setOwnerLoginId(channelUserVO.getOwnerLoginId());
                	personalDetails.setParentLoginId(channelUserVO.getParentLoginId());
					personalDetails.setAuthTypeAllowed(channelUserVO.getAuthTypeAllowed());
                	if(channelUserVO.getModifiedOn() != null) {
                		personalDetails.setModifiedOn(BTSLDateUtil.getLocaleDateTimeFromDate(channelUserVO.getModifiedOn()));
                	} 
                }
                
                // added by vikas (if in system preferences
                // PRF_ASSOCIATE_AGENT flag is true then user can modify his
                // agent only )
                // if value of flag is false then user can modify all the
                // user's below in the hierarchy
                if ("associate".equals(theForm.getRequestType()) && PretupsI.CHANNEL_USER_TYPE.equals(channelUserSessionVO.getUserType())) {
                    if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                        if (!(channelUserVO.getParentID().equals(channelUserSessionVO.getUserID()) && (channelUserVO.getCategoryVO().getCategoryType())
                                        .equals(PretupsI.AGENTCATEGORY))) {
                            final String arr2[] = { theForm.getSearchLoginId() };
                            log.error(methodName, "Error: Entere Login id is not of its agent's login id");
                            throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotagent", 0, arr2, null);
                        }
                    }
                }

                if ("associateOther".equals(theForm.getRequestType()) && PretupsI.CHANNEL_USER_TYPE.equals(channelUserSessionVO.getUserType())) {
                    if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                        if (!(channelUserVO.getParentID().equals(channelUserSessionVO.getUserID()) && (channelUserVO.getCategoryVO().getCategoryType())
                                        .equals(PretupsI.AGENTCATEGORY))) {
                            final String arr2[] = { theForm.getSearchLoginId() };
                            log.error(methodName, "Error: Entere Login id is not of its agent's login id");
                            throw new BTSLBaseException(this, methodName, "user.selectchanneluserforview.error.userloginidnotagent", 0, arr2, null);
                        }
                    }
                }
			}else if (!BTSLUtil.isNullString(theForm.getSearchCriteria())) {
				UserDAO userDAO = new UserDAO();
				ChannelUserDAO channelUserDAO = new ChannelUserDAO();
				ChannelUserVO channelUserVO = new ChannelUserVO();
				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUserDetailsByExtCode(con, theForm.getSearchCriteria(), null, statusUsed, status);
				}
				if (!BTSLUtil.isNullObject(channelUserVO)) {
					if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(channelUserVO.getUserType())) {
						final String arr2[] = {theForm.getSearchCriteria()};
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_INVALID_CCE, arr2);
					}

					boolean rsaRequired = false;
					rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
					theForm.setRsaRequired(rsaRequired);

					if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) && !PretupsI.USER_TYPE_STAFF.equals(channelUserVO.getUserType())) {
						final String arr2[] = {theForm.getExternalCode()};
						log.error(methodName, "Error: User are at the same level");
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_USER_SAME_LVL, arr2);
					}
					if (theForm.getSelectDomainList() != null) {
						final boolean isDomainFlag = this.isExistDomain(theForm.getSelectDomainList(), channelUserVO);
						if (!isDomainFlag) {
							final String arr2[] = {theForm.getSearchCriteria()};
							log.error(methodName, "Error: User not in the same domain");
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_USER_DOMAIN, arr2);
						}
					}
					final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
							channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());

					if (isGeoDomainFlag) {
						theForm.setCategoryVO(channelUserVO.getCategoryVO());
						theForm.setCategoryCode(theForm.getCategoryVO().getCategoryCode());
						theForm.setChannelCategoryCode(theForm.getCategoryVO().getCategoryCode());
						theForm.setCategoryCodeDesc(theForm.getCategoryVO().getCategoryName());
						theForm.setChannelCategoryDesc(theForm.getCategoryVO().getCategoryName());
						theForm.setParentDomainDesc(channelUserVO.getGeographicalDesc());
						this.setDetailsOnForm(theForm, con, userDAO, userwebDAO, channelUserVO, sessionUserVO);
					} else {
						final String arr2[] = { theForm.getSearchCriteria() };
						log.error(methodName, "Error: User not in the same domain");
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_GEO_ERROR, arr2);

					}
				} else {
					final String arr2[] = {theForm.getSearchCriteria()};
					log.error(methodName, "Error: User not exist");


					status = PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_NEW + "','" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
					if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
						channelUserVO = channelUserDAO.loadUserDetailsByExtCode(con, theForm.getExternalCode(), null, statusUsed, status);
					} else {
						final String userID = channelUserSessionVO.getUserID();
						channelUserVO = channelUserDAO.loadUserDetailsByExtCode(con, theForm.getExternalCode(), userID, statusUsed, status);
					}
					if (channelUserVO == null) {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_INVALID_CCE,  arr2);
					} else {
						throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTERNAL_CODE_USER_DELETED,  arr2);
					}

				}
			}

			setDatesToDisplayInForm(theForm);
            
        
        } catch(BTSLBaseException be) {
        	throw be;
        }  catch (Exception e) {
            //throw e;
        	throw new BTSLBaseException(e.getMessage());
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
    }
    
	public void setDatesToDisplayInForm(UserForm userForm) {
		UserForm thisForm = userForm;
		thisForm.setAppointmentDate(BTSLDateUtil.getSystemLocaleDate(thisForm.getAppointmentDate()));
	}
    
   /**
    * 
    * @param p_domainList
    * @param p_channelUserVO
    * @return
    * @throws Exception
    */
    private boolean isExistDomain(ArrayList p_domainList, ChannelUserVO p_channelUserVO) throws Exception {
        final String methodName = "isExistDomain";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_domainList.size()=" + p_domainList.size() + ", p_channelUserVO=" + p_channelUserVO);
        }
        if (p_domainList == null || p_domainList.isEmpty()) {
            return true;
        }
        boolean isDomainExist = false;
        try {
            ListValueVO listValueVO = null;
            for (int i = 0, j = p_domainList.size(); i < j; i++) {
                listValueVO = (ListValueVO) p_domainList.get(i);
                if (listValueVO.getValue().equals(p_channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
                    isDomainExist = true;
                    break;
                }
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            throw new BTSLBaseException(e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting isDomainExist=" + isDomainExist);
        }
        return isDomainExist;
    }

	
	
    /**
     * 
     * @param userForm
     * @param con
     * @param userDAO
     * @param userwebDAO
     * @param userVO
     * @param sessionUserVO
     * @throws Exception
     */
    private void setDetailsOnForm(UserForm theForm, Connection con, UserDAO userDAO, UserWebDAO userwebDAO, UserVO userVO, ChannelUserVO sessionUserVO) throws Exception {
        final String methodName = "setDetailsOnForm";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        
        theForm.setCreatedOn(BTSLUtil.getDateStringFromDate(userVO.getCreatedOn()));
        final CategoryDAO catDAO = new CategoryDAO();
        final ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
        
        theForm.setBatchID(userVO.getBatchID());
        theForm.setCreationType(userVO.getCreationType());
        if (!BTSLUtil.isNullString(userVO.getCreationType())) {
            theForm.setCreationTypeDesc(((LookupsVO) LookupsCache.getObject(PretupsI.USR_CREATION_TYPE, userVO.getCreationType())).getLookupName());
        }
        theForm.setUserId(userVO.getUserID());
        theForm.setChannelUserName(userVO.getUserName());
        theForm.setWebLoginID(userVO.getLoginID());
        theForm.setOldWebLoginID(userVO.getLoginID());
        theForm.setWebPassword(userVO.getPassword());
        theForm.setPasswordModifiedOn(userVO.getPasswordModifiedOn());
        // show ***** in the password field
        String passValue="";
        if (!BTSLUtil.isNullString(userVO.getPassword())) {
            // modified by ashishT
            if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                passValue = "********";
            } else {
                passValue = BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(userVO.getPassword()));
            }
        }
        theForm.setShowPassword(passValue);
        theForm.setConfirmPassword(passValue);
        theForm.setParentID(userVO.getParentID());
        theForm.setOwnerID(userVO.getOwnerID());
        theForm.setParentName(userVO.getParentName());
        theForm.setOwnerName(userVO.getOwnerName());
        theForm.setAllowedIPs(userVO.getAllowedIps());
        if (userVO.getAllowedDays() != null && userVO.getAllowedDays().trim().length() > 0) {
            theForm.setAllowedDays(userVO.getAllowedDays().split(","));
        }
        if (userVO.getPaymentTypes() != null && userVO.getPaymentTypes().trim().length() > 0) {
            theForm.setPaymentTypes(userVO.getPaymentTypes().split(","));
        }
        theForm.setAllowedFormTime(userVO.getFromTime());
        theForm.setAllowedToTime(userVO.getToTime());
        theForm.setEmpCode(userVO.getEmpCode());
        theForm.setStatus(userVO.getStatus());
        theForm.setStatusDesc(userVO.getStatusDesc());
        theForm.setPreviousStatus(userVO.getPreviousStatus());
        theForm.setEmail(userVO.getEmail());
        // Added by Deepika Aggarwal
        theForm.setCompany(userVO.getCompany());
        theForm.setFax(userVO.getFax());
        theForm.setUserLanguage(userVO.getLanguage());
        theForm.setFirstName(userVO.getFirstName());
        if (((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
            theForm.setLastName(userVO.getLastName());
        }

        theForm.setUserLanguageList(LocaleMasterDAO.loadLocaleMasterData());
        theForm.setDocumentTypeList(LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
        theForm.setPaymentTypeList(LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
        theForm.setContactNo(userVO.getContactNo());
        theForm.setDesignation(userVO.getDesignation());
		theForm.setContactPerson(userVO.getContactPerson());
        theForm.setMsisdn(userVO.getMsisdn());
        theForm.setUserType(userVO.getUserType());

        theForm.setAddress1(userVO.getAddress1());
        theForm.setAddress2(userVO.getAddress2());
        theForm.setCity(userVO.getCity());
        theForm.setState(userVO.getState());
        theForm.setCountry(userVO.getCountry());
        theForm.setRsaAuthentication(userVO.getRsaFlag());
        theForm.setSsn(userVO.getSsn());
        theForm.setUserNamePrefixCode(userVO.getUserNamePrefix());
        theForm.setExternalCode(userVO.getExternalCode());
        theForm.setShortName(userVO.getShortName());
        if (userVO.getAppointmentDate() != null) {
            theForm.setAppointmentDate(BTSLUtil.getDateStringFromDate(userVO.getAppointmentDate()));
        }
        theForm.setLastModified(userVO.getLastModified());
        theForm.setLevel1ApprovedBy(userVO.getLevel1ApprovedBy());
        theForm.setLevel1ApprovedOn(userVO.getLevel1ApprovedOn());
        theForm.setLevel2ApprovedBy(userVO.getLevel2ApprovedBy());
        theForm.setLevel2ApprovedOn(userVO.getLevel2ApprovedOn());
        theForm.setUserCode(userVO.getUserCode());
        /*
         * the userVO represents the data retrieved from the users table
         * so here we load the data from channleusers table, which returns a
         * channel user vo
         */
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        final ChannelUserVO channelUserVO = channelUserDAO.loadChannelUser(con, userVO.getUserID());
        theForm.setUserGradeId(channelUserVO.getUserGrade());
        theForm.setUserGradeName(channelUserVO.getUserGradeName());
        theForm.setTrannferProfileId(channelUserVO.getTransferProfileID());
        if (channelUserVO.getCommissionProfileSetID() != null) {
            theForm.setCommissionProfileSetId(channelUserVO.getCommissionProfileSetID().split(":")[0].toString());
        }

        // Added by Aatif
        if (((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
            theForm.setLmsProfileId((channelUserVO.getLmsProfile()));
            if (theForm.getLmsProfileId() != null) {
                theForm.setControlGroup(channelUserVO.getControlGroup());
            } else {
                theForm.setControlGroup(PretupsI.NO);
            }
        }
        theForm.setInsuspend(channelUserVO.getInSuspend());
        theForm.setOutsuspend(channelUserVO.getOutSuspened());
        theForm.setCategoryCode(channelUserVO.getCategoryCode());
        // theForm.setOutletCode(channelUserVO.getOutletCode());
        theForm.setCategoryCode(channelUserVO.getCategoryCode());
        // added for Authentication type
        theForm.setAuthTypeAllowed(channelUserVO.getAuthTypeAllowed());
        if ("N".equals(theForm.getIsCategoryCodeNeeded())) {
            theForm.setCategoryVO(catDAO.loadCategoryDetailsByCategoryCode(con, theForm.getCategoryCode()));
            theForm.setOutletCode(theForm.getCategoryVO().getOutletsAllowed());
            theForm.setChannelCategoryDesc(theForm.getCategoryVO().getCategoryName());
        } else {
            theForm.setOutletCode(channelUserVO.getOutletCode());
        }
        // added by akanksha for claro bug fix
        theForm.setOutletCode(channelUserVO.getOutletCode());
        theForm.setSubOutletCode(channelUserVO.getSubOutletCode());

        theForm.setLongitude(channelUserVO.getLongitude());
        theForm.setLatitude(channelUserVO.getLatitude());
        theForm.setDocumentType(channelUserVO.getDocumentType());
        theForm.setDocumentNo(channelUserVO.getDocumentNo());
        theForm.setPaymentType(channelUserVO.getPaymentType());
        
        if (channelUserVO.getPaymentTypes() != null && channelUserVO.getPaymentTypes().trim().length() > 0) {
            theForm.setPaymentTypes(channelUserVO.getPaymentTypes().split(","));
        }
        
        theForm.setMpayProfileID(channelUserVO.getMpayProfileID());
        theForm.setMcommerceServiceAllow(channelUserVO.getMcommerceServiceAllow());
        if (((Boolean)(PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
            theForm.setTrannferRuleTypeId(channelUserVO.getTrannferRuleTypeId());
        }
        if (!BTSLUtil.isNullString(channelUserVO.getUserGrade()) && !BTSLUtil.isNullString(channelUserVO.getMpayProfileID())) {
            theForm.setMpayProfileIDWithGrad(channelUserVO.getUserGrade() + ":" + channelUserVO.getMpayProfileID());
        }
        /*
         * if("N".equals(theForm.getIsCategoryCodeNeeded())){
         * if(TypesI.YES.equals(channelUserVO.getCatLowBalanceAlertAllow()))
         * theForm.setLowBalAlertAllow(channelUserVO.getLowBalAlertAllow());
         * }else{
         */
        // Added and modified by Amit Raheja for alerts
        theForm.setOtherEmail(channelUserVO.getAlertEmail());
        theForm.setLowBalAlertAllow(channelUserVO.getLowBalAlertAllow());
        theForm.setLowBalAlertToSelf("N");
        theForm.setLowBalAlertToParent("N");
        theForm.setLowBalAlertToOther("N");
        if (TypesI.YES.equals(theForm.getCategoryVO().getLowBalAlertAllow()) && TypesI.YES.equals(channelUserVO.getLowBalAlertAllow())) {
            final String alerttype = channelUserVO.getAlertType();
            if (alerttype != null) {
                final String[] alertTypeArr = alerttype.split(";");

                for (int k = 0; k < alertTypeArr.length; k++) {
                    alertTypeArr[k] = alertTypeArr[k].toUpperCase().trim();

                    if (alertTypeArr[k].equals((PretupsI.ALERT_TYPE_SELF))) {
                        theForm.setLowBalAlertToSelf("Y");
                    }
                    if (alertTypeArr[k].equals((PretupsI.ALERT_TYPE_OTHER))) {
                        theForm.setLowBalAlertToOther("Y");
                    }
                    if (alertTypeArr[k].equals((PretupsI.ALERT_TYPE_PARENT))) {
                        theForm.setLowBalAlertToParent("Y");
                    }
                }
            }
        }

        // Addition and modification ends
        // end Zebra and Tango
        // }
        /*
         * when we load the suboutlet drop downs
         * SuboutletCode is the combination
         * of lookup_code and sublookup_code so here we are combining the key
         */
//        if (!BTSLUtil.isNullString(channelUserVO.getSubOutletCode()) && !BTSLUtil.isNullString(channelUserVO.getOutletCode())) {
//            theForm.setSubOutletCode(channelUserVO.getSubOutletCode() + ":" + channelUserVO.getOutletCode());
//        }

        /*
         * Load other user details when requestType != delete and requestType !=
         * suspend and requestType != changeRole
         */


        /*
         * this is the case when this method called from the
         * addModify/showParentSearch/loadApprovalUser method
         * of the same class to view the userDetails
         * 
         * called from the addmodify when user perform search for the
         * user(through selectParentuser.jsp)
         * called from the showParentSearch when user load the information
         * through msisdn
         */
        /*
         * If outlets_allowed = Y of the selected category means user is
         * associated
         * with outlets and suboutlets
         * First we load the outlet and suboutlet dropdown(may these
         * dropdown aleardy loaded in the addmodify method
         * then we will load the outlet and suboutlet dropdown desc
         */
        if (TypesI.YES.equals(theForm.getCategoryVO().getOutletsAllowed())) {
            // load the outlet dropdown
            if (theForm.getOutletList() == null) {
                theForm.setOutletList(LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
            }

            if (theForm.getSubOutletList() == null) {
                final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
                theForm.setSubOutletList(sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
            }

            ListValueVO vo = null;
            // load the outlet dropdown desc
            if (theForm.getOutletList() != null) {
                vo = BTSLUtil.getOptionDesc(theForm.getOutletCode(), theForm.getOutletList());
                theForm.setOutletCodeDesc(vo.getLabel());
            }
            if (theForm.getSubOutletList() != null) {
                vo = BTSLUtil.getOptionDesc(theForm.getSubOutletCode(), theForm.getSubOutletList());
                theForm.setSubOutletCodeDesc(vo.getLabel());
            }

        }

        /*
         * May be dropdowns already loaded in the addModify method of the
         * same
         * wethther the dropdowm will be loaded or not is dependent on the
         * system preference value(UserApprovalLevel)
         */

        // load the Commision profile dropdown
        theForm.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(con, theForm.getCategoryCode(), userVO.getNetworkID(), theForm
                        .getGeographicalCode()));

        // for Zebra and Tango 2 by sanjeew Date 07/07/07
        /*
         * if(SystemPreferences.PTUPS_MOBQUTY_MERGD)
         * {
         * CommonUtil commonUtil=new CommonUtil();
         * theForm.setMpayProfileList(commonUtil.getMPayProfileArrayList(con,
         * theForm.getCategoryCode()));
         * }
         */
        // end Zebra and Tango

        final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
        // load the User Grade dropdown
        theForm.setUserGradeList(categoryGradeDAO.loadGradeList(con, theForm.getCategoryCode()));

        // load the Transfer Profile dropdown
        final TransferProfileDAO profileDAO = new TransferProfileDAO();
        theForm.setTrannferProfileList(profileDAO.loadTransferProfileByCategoryID(con, userVO.getNetworkID(), theForm.getCategoryCode(), PretupsI.PARENT_PROFILE_ID_USER));

        // load the userPrefixName dropdown
        theForm.setUserNamePrefixList(LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
        // added for user level transfer rule
        final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO.getNetworkID(), theForm
                        .getCategoryCode())).booleanValue();
        if (isTrfRuleTypeAllow) {
            theForm.setTrannferRuleTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
        }
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
        	// To load LMS Profile list.
            ArrayList lmsProfileList = channelUserWebDAO.getLmsProfileList(con, sessionUserVO.getNetworkID());
            theForm.setLmsProfileList( lmsProfileList );
        }

       

        // this method load the other details of the user
        this.loadDetails(theForm, con, userDAO, userwebDAO, userVO, sessionUserVO);
        // added by deepika aggarwal
        final ArrayList phoneList1 = theForm.getMsisdnList();
        if (phoneList1 != null && phoneList1.size() > 0) {
            final UserPhoneVO phoneVO = (UserPhoneVO) phoneList1.get(0);
            userVO.setLanguage(phoneVO.getPhoneLanguage() + "_" + phoneVO.getCountry());
            theForm.setPrimaryNumber(phoneVO.getPrimaryNumber());
            theForm.setShowSmsPin(BTSLUtil.decryptText(phoneVO.getSmsPin()));
            theForm.setConfirmSmsPin(BTSLUtil.decryptText(phoneVO.getSmsPin()));
        }

        theForm.setUserLanguage(userVO.getLanguage());
        
        // load the Description of the corresponding selected dropdown value
        this.setDropDownValue(theForm);
        
        // Added by md.sohail
        theForm.setNetworkCode(userVO.getNetworkID());
        theForm.setNetworkName(userVO.getNetworkName());
        theForm.setNetworkList(theForm.getNetworkList());
        // end of add by md.sohail
        
        theForm.setCategoryVO(userVO.getCategoryVO());
        theForm.setCategoryCode(theForm.getCategoryVO().getCategoryCode());
        theForm.setCategoryCodeDesc(theForm.getCategoryVO().getCategoryName());
        theForm.setParentName(userVO.getParentName());
        theForm.setParentMsisdn(userVO.getParentMsisdn());
        theForm.setParentCategoryName(userVO.getParentCategoryName());
        theForm.setOwnerName(userVO.getOwnerName());
        theForm.setOwnerMsisdn(userVO.getOwnerMsisdn());
        theForm.setOwnerCategoryName(userVO.getOwnerCategoryName());
        
        
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    }

    
    
	
	
    /**
     * 
     * @param userForm
     * @param p_con
     * @param p_userDAO
     * @param userwebDAO
     * @param p_userVO
     * @param sessionUserVO
     * @throws Exception
     */
    private void loadDetails( UserForm userForm, Connection p_con, UserDAO p_userDAO, UserWebDAO userwebDAO, UserVO p_userVO, ChannelUserVO sessionUserVO) throws Exception {
        final String methodName = "loadDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        

        final UserDAO userDAO = new UserDAO();
        final ChannelUserVO userChannelSessionVO = sessionUserVO;

        // load the phone info
        final ArrayList phoneList = p_userDAO.loadUserPhoneList(p_con, p_userVO.getUserID());
        if (phoneList != null && phoneList.size() > 0) {
        	userForm.setMsisdnList(phoneList);
        	userForm.setOldMsisdnList(phoneList);
        }

        final GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
        // load the geographies info from the user_geographies
        final ArrayList geographyList = _geographyDAO.loadUserGeographyList(p_con, p_userVO.getUserID(), p_userVO.getNetworkID());
        userForm.setGeographicalList(geographyList);

        if (geographyList.size() > 0) {
            /*
             * check whether the user has mutiple geographical area or not
             * if multiple then set into the zoneCode array
             * else set into the zone code
             */
            UserGeographiesVO geographyVO = null;
            if (TypesI.YES.equals(userForm.getCategoryVO().getMultipleGrphDomains())) {
                final String[] arr = new String[geographyList.size()];
                for (int i = 0, j = geographyList.size(); i < j; i++) {
                    geographyVO = (UserGeographiesVO) geographyList.get(i);
                    arr[i] = geographyVO.getGraphDomainCode();
                    userForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                }
                userForm.setGeographicalCodeArray(arr);
//                theForm.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(p_con, theForm.getCategoryCode(), userChannelSessionVO
//                                .getNetworkID(), theForm.getGeographicalCode()));

            } else {
                if (geographyList.size() == 1) {
                    geographyVO = (UserGeographiesVO) geographyList.get(0);
                    userForm.setGeographicalCode(geographyVO.getGraphDomainCode());
                    userForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                }
//                theForm.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(p_con, theForm.getCategoryCode(), userChannelSessionVO
//                                .getNetworkID(), theForm.getGeographicalCode()));

            }
        }

        // load the roles info from the user_roles table that are assigned with
        // the user
        final UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
        final ArrayList rolesList = rolesWebDAO.loadUserRolesList(p_con, p_userVO.getUserID());

        if (rolesList != null && rolesList.size() > 0) {
            final String[] arr = new String[rolesList.size()];
            rolesList.toArray(arr);
            userForm.setRoleFlag(arr);
        }
        /*
         * load the roles info from the category,category-roles and roles table
         * irrespective of the group_role flag
         * for showing the name of the role on the jsp
         */
        userForm.setRolesMap(rolesWebDAO.loadRolesList(p_con, p_userVO.getCategoryCode()));
        if (userForm.getRolesMap() != null && userForm.getRolesMap().size() > 0) {
            // this method populate the selected roles
            populateSelectedRoles(userForm);
        } else {
            // by default set Role Type = N(means System Role radio button will
            // be checked in edit mode if no role assigned yet)
            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.SYSTEM)) {
            	userForm.setRoleType("N");
            } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.GROUP)) {
            	userForm.setRoleType("Y");

            } else {
            	userForm.setRoleType("N");
            }
        }
        
        final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
        final ArrayList serviceList = servicesDAO.loadUserServicesList(p_con, p_userVO.getUserID());
        if (serviceList != null && serviceList.size() > 0) {
            final String[] arr = new String[serviceList.size()];
            for (int i = 0, j = serviceList.size(); i < j; i++) {
                final ListValueVO listVO = (ListValueVO) serviceList.get(i);
                // arr[i] = listVO.getValue();
                arr[i] = listVO.getLabel();
            }
            userForm.setServicesTypes(arr);
        }
        // load the services info from the service_type table that are asociated
        // with the user
        userForm.setServicesList(servicesDAO.loadServicesList(p_con, p_userVO.getNetworkID(), PretupsI.C2S_MODULE, userForm.getCategoryCode(), false));

        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue()) {
        	VomsProductDAO voucherDAO = new VomsProductDAO();
        	ArrayList voucherList = voucherDAO.loadUserVoucherTypeList(p_con, p_userVO.getUserID());

    		if (voucherList != null && !voucherList.isEmpty()) {
    			String[] arr = new String[voucherList.size()];
    			int voucherListSize = voucherList.size();
    			for (int i = 0, j = voucherListSize; i < j; i++) {
    				ListValueVO listVO = (ListValueVO) voucherList.get(i);
    				arr[i] = listVO.getLabel();
    			}
    			userForm.setVoucherTypes(arr);
    		}
    	
    		userForm.setVoucherList(voucherList);
        }
       
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    }
    
    /*
     * This method populate the selected roles from the map, which contains
     * all list of the roles
     */
    public void populateSelectedRoles(UserForm userForm) {
        final String methodName = "populateSelectedRoles";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        
        final UserForm theForm = userForm;
        final HashMap mp = theForm.getRolesMap();
        final HashMap newSelectedMap = new HashMap();
        final Iterator it = mp.entrySet().iterator();
        String key = null;
        ArrayList list = null;
        ArrayList listNew = null;
        UserRolesVO roleVO = null;
        Map.Entry pairs = null;
        boolean foundFlag = false;
        ViewUserRolesVO viewUserRolesVO = null;
        groupRolesMap = new HashMap();
        systemRolesMap = new HashMap();

        while (it.hasNext()) {
            pairs = (Map.Entry) it.next();
            key = (String) pairs.getKey();
            list = new ArrayList((ArrayList) pairs.getValue());
            listNew = new ArrayList();
            foundFlag = false;
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                    roleVO = (UserRolesVO) list.get(i);
                    if (theForm.getRoleFlag() != null && theForm.getRoleFlag().length > 0) { 
                        for (int k = 0; k < theForm.getRoleFlag().length; k++) {
                            if (roleVO.getRoleCode().equals(theForm.getRoleFlag()[k])) {
                                 // listNew.add(roleVO);
                            	viewUserRolesVO = new ViewUserRolesVO();
                            	viewUserRolesVO.setGroupRole(roleVO.getGroupRole());
                            	viewUserRolesVO.setRoleCode(roleVO.getRoleCode());
                            	viewUserRolesVO.setRoleName(roleVO.getRoleName());
                            	viewUserRolesVO.setGroupName(roleVO.getGroupName());
                            	viewUserRolesVO.setRoleType(roleVO.getRoleType());
                            	listNew.add(viewUserRolesVO);
                            	
                            	
                                foundFlag = true;
                                theForm.setRoleType(roleVO.getGroupRole());
                            }
                        }
                    }
                }
            }
            if (foundFlag) {
                newSelectedMap.put(key, listNew);
                if("Y".equals(roleVO.getGroupRole())) {
                	groupRolesMap.put(key, listNew);
                	
                } else if("N".equals(roleVO.getGroupRole())) {
                	systemRolesMap.put(key, listNew);
                }
            }
        }
        if (newSelectedMap.size() > 0) {
            theForm.setRolesMapSelected(newSelectedMap);
        } else {
            // by default set Role Type = N(means System Role radio button will
            // be checked in edit mode if no role assigned yet)
            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.SYSTEM)) {
                theForm.setRoleType("N");
            } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.GROUP)) {
                theForm.setRoleType("Y");

            } else {
                theForm.setRoleType("N");
            }
            theForm.setRolesMapSelected(null);
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
    }
    
    
    
    /**
     * This method set the dropdwon descriptions
     * 
     * @param form
     * @return void
     */
    private void setDropDownValue(UserForm theForm) {
        // load the Description of the corresponding selected dropdown value
        if ( !BTSLUtil.isNullOrEmptyList(theForm.getCommissionProfileList()) ) {
            final CommissionProfileSetVO vo = BTSLUtil.getOptionDescForCommProfile(theForm.getCommissionProfileSetId(), theForm.getCommissionProfileList());

            theForm.setCommissionProfileSetIdDesc(vo.getCommProfileSetName());
        }

        GradeVO gradeVO = null;
        if (theForm.getUserGradeList() != null) {
            for (int i = 0, j = theForm.getUserGradeList().size(); i < j; i++) {
                gradeVO = (GradeVO) theForm.getUserGradeList().get(i);
                if (gradeVO.getGradeCode().equals(theForm.getUserGradeId())) {
                    theForm.setUserGradeIdDesc(gradeVO.getGradeName());
                    break;
                }
            }
        }
        
        if ( !BTSLUtil.isNullOrEmptyList(theForm.getLmsProfileList()) ) {
            final ListValueVO listValueVO = BTSLUtil.getOptionDesc(theForm.getLmsProfileId(), theForm.getLmsProfileList());
            theForm.setLmsProfileListIdDesc(listValueVO.getLabel());
        }
        
        if ( !BTSLUtil.isNullOrEmptyList(theForm.getTrannferProfileList())) {
            final ListValueVO vo = BTSLUtil.getOptionDesc(theForm.getTrannferProfileId(), theForm.getTrannferProfileList());
            theForm.setTrannferProfileIdDesc(vo.getLabel());
        }
        
        // added for user level transfer rule
        final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, theForm.getNetworkCode(), theForm
                        .getCategoryCode())).booleanValue();
        if (isTrfRuleTypeAllow && theForm.getTrannferRuleTypeList() != null && theForm.getTrannferRuleTypeList().size() > 0) {
            final ListValueVO vo = BTSLUtil.getOptionDesc(theForm.getTrannferRuleTypeId(), theForm.getTrannferRuleTypeList());
            theForm.setTrannferRuleTypeIdDesc(vo.getLabel());
        }
        
        if (theForm.getUserNamePrefixList() != null && theForm.getUserNamePrefixList().size() > 0) {
            final ListValueVO vo = BTSLUtil.getOptionDesc(theForm.getUserNamePrefixCode(), theForm.getUserNamePrefixList());
            theForm.setUserNamePrefixDesc(vo.getLabel());
        }
        // added by deepika aggarwal
        if ( !BTSLUtil.isNullOrEmptyList(theForm.getUserLanguageList()) ) {
            final ListValueVO vo1 = BTSLUtil.getOptionDesc(theForm.getUserLanguage(), theForm.getUserLanguageList());
            theForm.setUserLanguageDesc(vo1.getLabel());
        }
        
        if ( !BTSLUtil.isNullOrEmptyList(theForm.getDocumentTypeList()) ) {
        	final ListValueVO vo2 = BTSLUtil.getOptionDesc(theForm.getDocumentType(), theForm.getDocumentTypeList());
        	theForm.setDocumentTypeDesc(vo2.getLabel());
        }
        
        if ((theForm.getPaymentType()!=null && !theForm.getPaymentType().isEmpty()) && !BTSLUtil.isNullOrEmptyList(theForm.getPaymentTypeList()) ) {
        	theForm.setPaymentTypeDesc(this.getOptionalDesc(theForm.getPaymentType().split(","), theForm.getPaymentTypeList()));
        }
        
        // for Zebra and Tango 2 by sanjeew Date 07/07/07
		/*
		 * if (SystemPreferences.PTUPS_MOBQUTY_MERGD) { if (theForm.getMpayProfileList()
		 * != null && theForm.getMpayProfileList().size() > 0) { if
		 * (!BTSLUtil.isNullString(theForm.getMpayProfileIDWithGrad()) &&
		 * theForm.getMpayProfileIDWithGrad().contains(":")) {
		 * theForm.setMpayProfileID((theForm.getMpayProfileIDWithGrad()).split(":")[1]);
		 * } else { theForm.setMpayProfileID(""); }
		 * 
		 * if (!BTSLUtil.isNullString(theForm.getMpayProfileID()) &&
		 * !BTSLUtil.isNullString(theForm.getUserGradeId())) {
		 * theForm.setMpayProfileIDWithGrad(theForm.getUserGradeId() + ":" +
		 * theForm.getMpayProfileID()); }
		 * 
		 * theForm.setMpayProfileDesc((BTSLUtil.getOptionDesc(theForm.
		 * getMpayProfileIDWithGrad(), theForm.getMpayProfileList())).getLabel()); } }
		 */
        // end Zebra and Tango
    }
    
    /**
     * 
     * @param p_codeArr
     * @param p_list
     * @return
     */
	public String getOptionalDesc(String[] p_codeArr, List p_list) {

		if (log.isDebugEnabled()) {
			log.debug("getOptionalDesc", "Entered: p_code=" + p_codeArr + " p_list=" + p_list);
		}
		ListValueVO vo = null;
		StringBuilder optionalDesc = new StringBuilder();

		if (!BTSLUtil.isNullorEmpty(p_codeArr) && p_list != null && !p_list.isEmpty()) {

			for (String p_codeDesc : p_codeArr) {
				for (int i = 0, j = p_list.size(); i < j; i++) {
					vo = (ListValueVO) p_list.get(i);
					if (vo.getValue().equalsIgnoreCase(p_codeDesc)) {
						optionalDesc.append(vo.getLabel() + ",");
					}
				}

			}
		}

		optionalDesc.deleteCharAt(optionalDesc.lastIndexOf(","));
		if (log.isDebugEnabled()) {
			log.debug("getOptionalDesc", "Exited: optionalDesc=" + optionalDesc);
		}

		return optionalDesc.toString();
	}
    
    
    /**
     * 
     * @param userForm
     * @param personalDetails
     * @param loginDetails
     * @param paymentAndServiceDetails
     * @param profileDetails
     * @param groupedUserRoles
     */
    private void setUserDetailsResponse(UserForm userForm, PersonalDetailsVO personalDetails, LoginDetailsVO loginDetails, PaymentAndServiceDetailsVO paymentAndServiceDetails,
    		ProfileDetailsVO profileDetails, GroupedUserRolesVO groupedUserRoles) {
    	
    	if(log.isDebugEnabled()) {
			log.debug("setUserDetailsResponse", "Entered... ");
		}
    	/*
		 * Setting User Personal Details
		 */
    	ArrayList geo = new ArrayList();
    	
    	String data=null;
    	UserGeographiesVO geodata=new UserGeographiesVO();
    	if(!BTSLUtil.isNullOrEmptyList(userForm.getGeographicalList())) {
    	geodata = (UserGeographiesVO) userForm.getGeographicalList().get(0);
    	data = geodata.getGraphDomainName();
    	}
		personalDetails.setAddressLine1(userForm.getAddress1());
		personalDetails.setAddressLine2(userForm.getAddress2());
		personalDetails.setAppointmentDate(userForm.getAppointmentDate());
		personalDetails.setCategoryCode(userForm.getCategoryCode());
		personalDetails.setCategoryCodeDesc(userForm.getCategoryCodeDesc());
		personalDetails.setCity(userForm.getCity());
		personalDetails.setCompany(userForm.getCompany());
		personalDetails.setOtherEmailId(userForm.getOtherEmail());
		personalDetails.setEmpCode(userForm.getEmpCode());
		personalDetails.setContactPerson(userForm.getContactPerson());
		personalDetails.setCountry(userForm.getCountry());
		personalDetails.setDocumentNo(userForm.getDocumentNo());
		personalDetails.setDocumentType(userForm.getDocumentType());
		personalDetails.setDomainCode(userForm.getDomainCode());
		personalDetails.setDomainCodeDesc(userForm.getDomainCodeDesc());
		personalDetails.setEmailId(userForm.getEmail());
		personalDetails.setExternalCode(userForm.getExternalCode());
		personalDetails.setFax(userForm.getFax());
		personalDetails.setFirstName(userForm.getFirstName());
		//personalDetails.setGeography(userForm.getParentDomainDesc());
		//
		personalDetails.setGeography(data);
		//
		personalDetails.setGeographyCode(userForm.getGeographicalCode());

		personalDetails.setLastName(userForm.getLastName());
		personalDetails.setLatitude(userForm.getLatitude());
		personalDetails.setLongitude(userForm.getLongitude());
		personalDetails.setMsisdn(userForm.getMsisdn());
		personalDetails.setOwnerName(userForm.getOwnerName());
		personalDetails.setParentCategory(userForm.getParentCategoryName());
		personalDetails.setParentName(userForm.getParentName());
		personalDetails.setSsn(userForm.getSsn());
		personalDetails.setState(userForm.getState());
		personalDetails.setStatus(userForm.getStatus());
		personalDetails.setStatusDesc(userForm.getStatusDesc());
		personalDetails.setSubscriberCode(userForm.getEmpCode()); // subscriber code = employee code
		personalDetails.setUserId(userForm.getUserId());
		personalDetails.setShortName(userForm.getShortName());
		personalDetails.setNamePrefix(userForm.getUserNamePrefixCode());
		personalDetails.setLanguage(userForm.getUserLanguageDesc());
		personalDetails.setUserLanguage(userForm.getUserLanguage());
		personalDetails.setUserLanguageDesc(userForm.getUserLanguageDesc());
		personalDetails.setCreationType(userForm.getCreationType());
		personalDetails.setCreationTypeDesc(userForm.getCreationTypeDesc());
		personalDetails.setUserName(userForm.getChannelUserName());
		personalDetails.setContactNumber(userForm.getContactNo());
		personalDetails.setDesignation(userForm.getDesignation());
		personalDetails.setOutletCode(userForm.getOutletCode());
		personalDetails.setOutletCodeDesc(userForm.getOutletCodeDesc());
		personalDetails.setSubOutletCode(userForm.getSubOutletCode());
		personalDetails.setSubOutletCodeDesc(userForm.getSubOutletCodeDesc());
		personalDetails.setParentGeographyCode(userForm.getParentDomainCode());
		personalDetails.setCreatedOn(userForm.getCreatedOn());
		personalDetails.setCreatedBy(userForm.getCreatedBy());
		personalDetails.setDepartment(userForm.getDepartmentDesc());
		personalDetails.setAppointmentDate(userForm.getAppointmentDate());
		personalDetails.setDivision(userForm.getDivisionDesc());
		personalDetails.setDepartmentCode(userForm.getDepartmentCode());
		personalDetails.setDivisionCode(userForm.getDivisionCode());
		
		/*
		 * Setting login details
		 */
		loginDetails.setAllowedDays(userForm.getAllowedDays());
		loginDetails.setAllowedIp(userForm.getAllowedIPs());
		loginDetails.setAllowedFromTime(userForm.getAllowedFormTime());
		loginDetails.setAllowedToTime(userForm.getAllowedToTime());
		loginDetails.setLoginId(userForm.getWebLoginID());
		loginDetails.setUserCode(userForm.getUserCode());
		loginDetails.setNetworkCode(userForm.getNetworkCode());
		
		/*
		 * Setting Login Details
		 */
		ArrayList<String> secMsisdnList = new ArrayList<>();
		PhoneDetails phoneDetails =null;
		ArrayList<PhoneDetails> secMsisdnListWithDetail = new ArrayList<>();
		ArrayList phoneVOList = userForm.getMsisdnList();
		
		if ( !BTSLUtil.isNullOrEmptyList(phoneVOList) ) {
			
				for(int listIndex = 0; listIndex < phoneVOList.size(); listIndex++ ) {
					UserPhoneVO phoneVO = (UserPhoneVO) phoneVOList.get(listIndex);
					
					if("N".equalsIgnoreCase(phoneVO.getPrimaryNumber())) 
					{
						secMsisdnList.add(phoneVO.getMsisdn());
						
						//new Requirement
						phoneDetails = new PhoneDetails();
						phoneDetails.setMsisdn(phoneVO.getMsisdn());
						phoneDetails.setDesc(phoneVO.getDescription());
						phoneDetails.setInvalidPinCount(phoneVO.getInvalidPinCount());
						phoneDetails.setProfileName(phoneVO.getPhoneProfileDesc());
						phoneDetails.setIsPrimary(phoneVO.getPrimaryNumber());
						phoneDetails.setUserPhoneId(phoneVO.getUserPhonesId());
						phoneDetails.setPin(AESEncryptionUtil.aesEncryptor(BTSLUtil.decryptText(phoneVO.getSmsPin()), Constants.A_KEY));
						
						secMsisdnListWithDetail.add(phoneDetails);
					} else 
					{ // only one primary number at a time
						loginDetails.setPrimaryMsisdn(phoneVO.getMsisdn());
						loginDetails.setProfileName(phoneVO.getPhoneProfileDesc());
						loginDetails.setIsPrimary(phoneVO.getPrimaryNumber());
						loginDetails.setDescription(phoneVO.getDescription());
						loginDetails.setInvalidPinCount(phoneVO.getInvalidPinCount());
						loginDetails.setUserPhoneId(phoneVO.getUserPhonesId());
						if(!phoneVO.getSmsPin().equalsIgnoreCase(PretupsI.NOT_AVAILABLE))
							loginDetails.setPin(AESEncryptionUtil.aesEncryptor(BTSLUtil.decryptText(phoneVO.getSmsPin()), Constants.A_KEY));
						
					}
			}
				loginDetails.setSecMsisdn(secMsisdnList);
				loginDetails.setSecMsisdnWithDetail(secMsisdnListWithDetail);
		}	
				
		paymentAndServiceDetails.setVoucherType(userForm.getVoucherTypes());
		
		/*
		 * Loading Payment Modes
		 */
		paymentAndServiceDetails.setPaymentModes(userForm.getPaymentTypes());
		paymentAndServiceDetails.setPaymentType(userForm.getPaymentType());
		paymentAndServiceDetails.setPaymentTypes(userForm.getPaymentTypes());
		paymentAndServiceDetails.setPaymentDesc(userForm.getPaymentTypeDesc());

		/*
		 * load the services info from the user_services table that are assigned to the
		 * user SETTING USER SERVICE LIST
		 */

		paymentAndServiceDetails.setServiceInformation(userForm.getServicesTypes());
		paymentAndServiceDetails.setServiceTypes(userForm.getServicesTypes());
		
		
		/*
		 * Setting user roles and role type: system role or group role
		 */
		groupedUserRoles.setRoleType(userForm.getRoleType());
		 if ("N".equals(userForm.getRoleType())) {
             groupedUserRoles.setRoleTypeDesc("System Roles");
         } else {
             groupedUserRoles.setRoleTypeDesc("Group Role");

         }
		groupedUserRoles.setGroupRolesMap(groupRolesMap);
		groupedUserRoles.setSystemRolesMap(systemRolesMap);
		

		/*
		 * Commission profile Details
		 */
		profileDetails.setCommissionProfile(userForm.getCommissionProfileSetIdDesc());
		profileDetails.setCommissionProfileSetId(userForm.getCommissionProfileSetId());
		profileDetails.setCommissionProfileSetIdDesc(userForm.getCommissionProfileSetIdDesc());

		/*
		 * Loading and Setting Transfer Profile Details
		 */
		profileDetails.setTransferRuleType(userForm.getTrannferRuleTypeIdDesc());
		profileDetails.setTransferRuleTypeId(userForm.getTrannferRuleTypeId());
		profileDetails.setTransferRuleTypeIdDesc(userForm.getTrannferRuleTypeIdDesc());
		
		profileDetails.setUserGrade(userForm.getUserGradeId());
		profileDetails.setUserGradeName(userForm.getUserGradeName());
		profileDetails.setTransferProfile(userForm.getTrannferProfileIdDesc());
		profileDetails.setTransferProfileId(userForm.getTrannferProfileId());
		profileDetails.setTransferProfilIdDesc(userForm.getTrannferProfileIdDesc());
		

		/*
		 * SETTING SUSPENSION RIGHTS and BALANCE ALERT
		 */
		paymentAndServiceDetails.setInboundSuspensionRights(userForm.getInsuspend());
		paymentAndServiceDetails.setOuboundSuspensionRights(userForm.getOutsuspend());
		paymentAndServiceDetails.setLowBalanceAlertToSelf(userForm.getLowBalAlertAllow());
	    paymentAndServiceDetails.setLowBalanceAlertToParent(userForm.getLowBalAlertToParent());
		paymentAndServiceDetails.setLowBalanceAlertToOthers(userForm.getLowBalAlertToOther());
		
    	
    }
    
    
    /**
     * 
     * @param con
     * @param personalDetails
     * @param userId
     * @throws BTSLBaseException
     */
    private void loadUserBalances(Connection con, PersonalDetailsVO personalDetails, String userId) throws BTSLBaseException {
    	
    	if(log.isDebugEnabled()) {
			log.debug("loadUserBalances", "Entered... ");
		}
    	
		final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
		ArrayList<C2sBalanceQueryVO> userBalanceList = userBalancesDAO.loadUserBalances(con, userId);
		long userOtherBalance = 0;

		if (!BTSLUtil.isNullorEmpty(userBalanceList)) {
			for (C2sBalanceQueryVO balanceVO : userBalanceList) 
			{
				if (PretupsI.PRODUCT_ETOPUP.equals(balanceVO.getProductCode())) 
				{
					personalDetails.setPrepaidBalance(PretupsBL.getDisplayAmount(balanceVO.getBalance()));
				} else if (PretupsI.PRODUCT_POSTETOPUP.equals(balanceVO.getProductCode())) 
				{
					personalDetails.setPostpaidBalance(PretupsBL.getDisplayAmount(balanceVO.getBalance()));
				} else {
					userOtherBalance += balanceVO.getBalance();
				}
			}
		}

		personalDetails.setUserOtherBalance(PretupsBL.getDisplayAmount(userOtherBalance ));
		personalDetails.setUserBalanceList(userBalanceList);
    }
    
    
    /**
     * 
     * @param con
     * @param userId
     * @return
     * @throws BTSLBaseException
     */
    private String[] loadUserWidget( Connection con, String userId ) throws BTSLBaseException {
    	
    	if(log.isDebugEnabled()) {
			log.debug("loadUserWidget", "Entered... ");
		}
    	
    	final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
    	ArrayList<String> widgetlist = channelUserDAO.loadUserWigets(con, userId);
		String[] arr = new String[widgetlist.size()];
		
		// ArrayList to Array Conversion
		for (int i = 0; i < widgetlist.size(); i++)
			arr[i] = widgetlist.get(i);
		
		return arr;
    }
    
    
    /**
     * 
     * @param con
     * @param userForm
     * @return
     * @throws BTSLBaseException
     */
    private BarredUserDetailsVO loadBarredUserDetails(Connection con, UserForm userForm ) throws BTSLBaseException {
    	
    	if(log.isDebugEnabled()) {
			log.debug("BarredUserDetailsVO", "Entered... ");
		}
    	
    	if (!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(userForm.getStatus())) {
    		return null;
    	}
    	
    	BarredUserDetailsVO barredUserDetails = new BarredUserDetailsVO();
    	final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

		final ArrayList<BarredUserDetailsVO> barredUserList = channelUserDAO.loadBarredUserDetails(con,
				userForm.getUserId(), PretupsI.SUSPEND_EVENT_APPROVAL, PretupsI.MODULE_TYPE, userForm.getNetworkCode());

		if (!BTSLUtil.isNullOrEmptyList(barredUserList)) {
			barredUserDetails = barredUserList.get(0);
		}
		return barredUserDetails;
    }
    
    
    private void loadGeography(Connection p_con,UserForm userForm,FetchUserDetailsResponseVO response) throws BTSLBaseException {
    	if(log.isDebugEnabled()) {
			log.debug("loadGeography", "Entered... ");
		}
    	
    	GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
        GeographicalDomainWebDAO _geographyDomainWebDAO = new GeographicalDomainWebDAO();
         ArrayList<UserGeographiesVO> geographyList = new ArrayList();
         if(!((TypesI.SUPER_NETWORK_ADMIN.equalsIgnoreCase(userForm.getCategoryCode()))||(TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(userForm.getCategoryCode()))))
         {
         // load the geographies info from the user_geographies
         	 
         	if( (TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userForm.getCategoryCode())))
         	{
         		// case when we added super channel admin from super admin, wish to load geographies that were assigned (irrespective of network code) to super channel admin
         		 geographyList = _geographyDAO.loadUserGeographyListForSuperChannelAdmin(p_con, userForm.getUserId());
         	}
         	else
         	{
         		 geographyList =(ArrayList<UserGeographiesVO>)_geographyDAO.loadUserGeographyList(p_con,  userForm.getUserId(), userForm.getNetworkCode());
         	}
         	response.setNetOrGeoCodes(geographyList.stream().map(p->p.getGraphDomainCode()).collect(Collectors.toList()));
         	if (geographyList != null && geographyList.size() > 0)
         	{
             /*
              * check whether the user has mutiple geographical area or not if
              * multiple then set into the zoneCode array else set into the zone
              * code
              */
         		
         		UserGeographiesVO geographyVO = null;
         		if (TypesI.YES.equals(userForm.getCategoryVO().getMultipleGrphDomains())) 
         		{
         			
         			String[] arr = new String[geographyList.size()];
         			int geographyListSize = geographyList.size();
         			for (int i = 0, j = geographyListSize; i < j; i++)
         			{
         				geographyVO = (UserGeographiesVO) geographyList.get(i);
         				arr[i] = geographyVO.getGraphDomainCode();
         				userForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
         			}
         			userForm.setGeographicalCodeArray(arr);
         		}
         		else 
         		{
         			if (geographyList.size() == 1)
         			{
         				geographyVO = (UserGeographiesVO) geographyList.get(0);
         				userForm.setGeographicalCode(geographyVO.getGraphDomainCode());
         				userForm.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
         			}
         		}
         	}
         	response.setGeographies(geographyList);
         	  if(log.isDebugEnabled()) {
       			log.debug("loadGeography", "Exit... ");
       		}
         	
         }
         else
         {
         	//case when we added operator users: super network admin and super cce from super admin and wish load their details (networklist is loaded instead of geographies)
         	// load the geographies info from the user_geographies
             ArrayList<UserGeographiesVO> networkList = _geographyDAO.loadUserNetworkList(p_con, userForm.getUserId());
             response.setNetOrGeoCodes( networkList.stream().map(p->p.getGraphDomainCode()).collect(Collectors.toList()));
             userForm.setGeographicalList(networkList);
             if (networkList != null && networkList.size() > 0) {
                 	UserGeographiesVO geographyVO = null;
                     String[] arr = new String[networkList.size()];
                     int   networkListSize = networkList.size();
                     for (int i = 0, j = networkListSize; i < j; i++) {
                         geographyVO = networkList.get(i);
                         arr[i] = geographyVO.getGraphDomainCode();
                     }
                     userForm.setGeographicalCodeArray(arr);
                     ArrayList netVOList = new NetworkDAO().loadNetworkList(p_con, "'"+PretupsI.STATUS_DELETE+"'");
             		networkList = new ArrayList();
             		int netVOListSize = netVOList.size();
             		for(int i=0; i< netVOListSize; i++) {
             			NetworkVO netVo = (NetworkVO)netVOList.get(i);
             			UserGeographiesVO geogVO = new UserGeographiesVO();
             			geogVO.setGraphDomainCode(netVo.getNetworkCode());
             			geogVO.setGraphDomainName(netVo.getNetworkName());
             			networkList.add(geogVO);
             		}
             		userForm.setNetworkList(networkList);
                 }
         	response.setGeographies(networkList);
             if(log.isDebugEnabled()) {
      			log.debug("loadGeography", "Exit... ");
      		}
             
         }
    }
    
    
    //to load all vouchersegment and domain details
    private void loadDomainServiceAndVoucherSegment(Connection p_con,UserForm userForm,FetchUserDetailsResponseVO response)throws BTSLBaseException {
    	
    	/////////////////////////
    	//domain
    	
    	DomainDAO domainDAO = new DomainDAO();
        DomainWebDAO domainWebDAO = new DomainWebDAO();
        LookupsDAO lookupsDao=new LookupsDAO();
        ArrayList domainList = domainWebDAO.loadUserDomainList(p_con, userForm.getUserId());
        if (domainList != null && domainList.size() > 0) {
            String[] arr = new String[domainList.size()];
            domainList.toArray(arr);
            userForm.setDomainCodes(arr);
            response.setDomainCodes(Arrays.asList(arr));
        }
        // load the domain info from the domain table that are associated with
        // the users
        userForm.setDomainList(domainDAO.loadDomainList(p_con, PretupsI.DOMAIN_TYPE_CODE));
        response.setDomainList(userForm.getDomainList());
        
        ///////////////////////////////////////////////////////////////////////
        //service
        ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
        ArrayList serviceList = servicesDAO.loadUserServicesList(p_con, userForm.getUserId());
       
        if (serviceList.size() > 0 && (userForm.getCategoryCode().equalsIgnoreCase(PretupsI.OPERATOR_CATEGORY) || userForm.getCategoryCode().equalsIgnoreCase(PretupsI.SUPER_CHANNEL_ADMIN))) {
            // theForm.setIsSerAssignChnlAdm(true);
            if (serviceList != null && serviceList.size() > 0) {
                String[] arr = new String[serviceList.size()];
                int serviceListSize  = serviceList.size();
                for (int i = 0, j = serviceListSize; i < j; i++) {
                    ListValueVO listVO = (ListValueVO) serviceList.get(i);
                    arr[i] = listVO.getValue();
                }
                userForm.setServicesTypes(arr);
                response.setServicesTypes(Arrays.asList(arr));
            }
        }
        /*
         * load all services irrespective of the module_code becs operator user
         * can be associate with P2P as well as C2S services
         */
        if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(userForm.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(userForm.getCategoryCode())) {
        	userForm.setServicesList(servicesDAO.assignServicesToChlAdmin(p_con, userForm.getNetworkCode()));
        } else {
        	userForm.setServicesList(servicesDAO.loadServicesList(p_con, userForm.getNetworkCode()));
        }
        response.setServicesList(userForm.getServicesList());
        
        /////////////////////////////////////////////////////////////
        // load the Products info from the user_products table that are assigned
        // to the user
        ProductTypeDAO productTypeDAO = new ProductTypeDAO();
        ArrayList productList = productTypeDAO.loadUserProductsList(p_con,userForm.getUserId());
        if (productList != null && productList.size() > 0) {
            String[] arr = new String[productList.size()];
            productList.toArray(arr);
            userForm.setProductCodes(arr);
            response.setProductsCodes(Arrays.asList(arr));
        }
        // load the products list that are asociated with the Network and Module
        // NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        // theForm.setProductsList(networkProductDAO.loadProductListByNetIdANDModuleCode(p_con,p_userVO.getNetworkID(),PretupsI.C2S_MODULE));
       // userForm.setProductsList(LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true));
        List<LookupsVO> listLookups=lookupsDao.loadLookupsFromLookupType(p_con,PretupsI.PRODUCT_TYPE);
        response.setProductsList(listLookups);
        ////////////////////////////////////////////////////////////////////////////
        
        // load the voucher type info from the user_vouchertype table that are assigned to the user
        VomsProductDAO  voucherDAO = new VomsProductDAO();
        boolean userVoucherTypeAllowed = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
        if(userVoucherTypeAllowed)
        {
        ArrayList voucherList = voucherDAO.loadUserVoucherTypeList(p_con,userForm.getUserId());
       
        if (voucherList.size() > 0) {
            // theForm.setIsSerAssignChnlAdm(true);
            if (voucherList != null && voucherList.size() > 0) {
                String[] arr = new String[voucherList.size()];
                int voucherListSize  = voucherList.size();
                for (int i = 0, j = voucherListSize; i < j; i++) {
                    ListValueVO listVO = (ListValueVO) voucherList.get(i);
                    arr[i] = listVO.getValue();
                }
                userForm.setVoucherTypes(arr);
                response.setVoucherTypes(Arrays.asList(arr));
            }
        }
        /*
         * load all voucher type
         */
		 
        userForm.setVoucherList(voucherDAO.loadVoucherTypeList(p_con));
        response.setVoucherList(userForm.getVoucherList());
        }
        ArrayList segmentList = voucherDAO.loadUserVoucherSegmentList(p_con,userForm.getUserId());
        if (segmentList != null && segmentList.size() > 0) {
                String[] arr = new String[segmentList.size()];
                int segmentListSize  = segmentList.size();
                for (int i = 0, j = segmentListSize; i < j; i++) {
                    ListValueVO listVO = (ListValueVO) segmentList.get(i);
                    arr[i] = listVO.getValue();
                }
                userForm.setSegments(arr);
                response.setSegments(Arrays.asList(arr));
            }
        
        userForm.setSegmentList(LookupsCache.loadLookupDropDown(VOMSI.VOUCHER_SEGMENT, true));
        response.setSegmentList(userForm.getSegmentList());
    }


}

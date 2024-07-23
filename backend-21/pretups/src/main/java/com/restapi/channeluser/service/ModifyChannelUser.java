package com.restapi.channeluser.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channeluser.businesslogic.ModifyChannelUserRequestVO;
import com.btsl.pretups.channeluser.businesslogic.Msisdn;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SqlParameterEncoder;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


/*@Path("/v1/channelUsers")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${ModifyChannelUser.name}", description = "${ModifyChannelUser.desc}")//@Api(tags= "Channel Users", value="Channel Users")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class ModifyChannelUser {
    private static final Log LOG = LogFactory.getLog(ModifyChannelUser.class.getName());
    private ChannelUserVO channelUserVO = null;
    private UserDAO userDAO = null;
    private CategoryVO userCategoryVO = null;
    private ChannelUserDAO channelUserDao = null;
    private ExtUserDAO extUserDao = null;
    private ChannelUserVO parentChannelUserVO = null;
    private ChannelUserVO modifiesChannelUserVO = null;
    private ChannelUserVO senderVO = null;
    private static final String CLASSNAME = "ModifyChannelUser";

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param requestVO
     */
    @SuppressWarnings("unchecked")
	/*@PUT
    @Path("/{id}")*/
    @PostMapping(value = "/modify", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	@ResponseBody
    /*@Consumes(value=MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
    /*@ApiOperation(tags= "Channel Users", value = "Modify Channel User",
	  notes = ("Api Info:") + ("\n") + ("1. roleType:'N' signifies System Role, 'Y' signifies Group Role") + ("\n") + 
				("2. roles:It will contain comma separated values for roles") + ("\n") + 
				 ("3. services:It will contain comma separated services code")+ ("\n") +
				 ("4. paymentType:It will contain comma separated payment type modes")+("\n") +
				 ("5. voucherTypes:It will contain comma separated voucher types")+("\n") +
				 ("6. tags(a):commissionProfileID,transferRuleType,transferProfile,control group,lmsProfileID will be activated if SystemPreference Name: " + ("\n") + "LMS APPLICABLE is true, Transfer Rule For User Level is true, USER_APPROVAL=0")+("\n") +
				 ("7. If Associate Profile has been associated to the user then also tags(a) will become mandatory to be filled "),
				 authorizations = {
		    	            @Authorization(value = "Authorization")} )
            @ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = BaseResponse.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/
    @io.swagger.v3.oas.annotations.Operation(summary = "${modify.summary}", description="${modify.description}",

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

    public BaseResponse modifyChannelUser(
	@Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,	 
	 @Parameter(description = SwaggerAPIDescriptionI.USR_SELECT_PUT, required = true)//allowableValues = "USERLOGIID,USERMSISDN,EXTCODE")
	@RequestParam("idType") String idtype,
	 @Parameter(description = SwaggerAPIDescriptionI.SELECTED_VALUE_PUT, required = true)
	@RequestParam("idValue") String id,
	 @Parameter(description = SwaggerAPIDescriptionI.MODIFY_CHANNEL_USER, required = true)
	@RequestBody ModifyChannelUserRequestVO requestVO, HttpServletResponse responseSwag) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "modifyChannelUser";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, "Entered ");
		}
        Connection con = null;MComConnectionI mcomCon = null;
        channelUserDao = new ChannelUserDAO();
        channelUserVO = new ChannelUserVO();
        userDAO = new UserDAO();
        extUserDao = new ExtUserDAO();
        OperatorUtilI operatorUtili = null;
        Locale locale = null;
        ArrayList oldPhoneList = null;
        String senderPin = "";
        String defaultGeoCode = "";
        OAuthUser oAuthUser= null;
		OAuthUserData oAuthUserData =null;
		String identifierValue = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                    "Exception while loading the class at the call:" + e.getMessage());
        }
        BaseResponse response = null;
        try {
        	
        	
			
        	mcomCon = new MComConnection();con=mcomCon.getConnection();

        	response = new BaseResponse();
        	
        	/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
        	oAuthUser = new OAuthUser();
			oAuthUserData = new OAuthUserData();
			oAuthUser.setData(oAuthUserData);
			OAuthenticationUtil.validateTokenApi(oAuthUser,headers,responseSwag);
			 
			identifierValue= oAuthUser.getData().getLoginid();
        	identifierValue =  SqlParameterEncoder.encodeParams(identifierValue);
        
			final String status = "'" + PretupsI.STATUS_CANCELED + "','" + PretupsI.STATUS_DELETE + "'";
			UserDAO userDao = new UserDAO();

			senderVO=(ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con,identifierValue);
			
            // Load details of channel user to be modified
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            String userExtCode=null;
            String modifiedUserLoginId = null;
            String modifiedUserMsisdn = null;
            if(idtype.equalsIgnoreCase("EXTCODE"))
            {
            	userExtCode=id;
            	userExtCode = userExtCode.trim();
            }
            else if(idtype.equalsIgnoreCase("USERMSISDN"))
            {
            	modifiedUserMsisdn=id;
            	 modifiedUserMsisdn = PretupsBL.getFilteredMSISDN(modifiedUserMsisdn);
            	 
            }
            else if(idtype.equalsIgnoreCase("USERLOGIID"))
            {
            	modifiedUserLoginId=id;
            	 modifiedUserLoginId = modifiedUserLoginId.trim();
            }
            
            // Load Channel User on basis of Primary MSISDN only:
            if(BTSLUtil.isNullString(modifiedUserMsisdn) && BTSLUtil.isNullString(modifiedUserLoginId) && BTSLUtil.isNullString(userExtCode)){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DETAILS_BLANK);
            }
            modifiesChannelUserVO = extUserDao.loadChannelUserDetailsByMsisdnLoginIdExt(con, modifiedUserMsisdn, modifiedUserLoginId, null, userExtCode, locale);
            if (!(modifiesChannelUserVO == null)) {
                oldPhoneList = userDAO.loadUserPhoneList(con, modifiesChannelUserVO.getUserID());
                channelUserVO = modifiesChannelUserVO;
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER); // Message
                // Changes
                // on
                // 11-MAR-2014
            }
            UserWebDAO userWebDAO = new UserWebDAO();
            ChannelUserDAO channelUserDAO =new ChannelUserDAO();
            ChannelUserVO channelUserVO1 =null;
            if (PretupsI.OPERATOR_TYPE_OPT.equals(senderVO.getDomainID())) {
            	ChannelUserVO ownerChannelrUserVO = userDao.loadUserDetailsFormUserID(con, channelUserVO.getOwnerID());
            	senderVO=(ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con,ownerChannelrUserVO.getLoginID());	
            	channelUserVO1 = channelUserDAO.loadUsersDetailsByLoginId(con, channelUserVO.getLoginID(), null, "NOT IN", "'N','C'");
            } else {
            	channelUserVO1 = channelUserDAO.loadUsersDetailsByLoginId(con, channelUserVO.getLoginID(), senderVO.getUserID(), "NOT IN", "'N','C','DR'");
            }
            if(channelUserVO1==null)
            {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_NOT_IN_HIERARCHY);
            }
            
            String userCatCode = channelUserVO.getCategoryCode();
            userCatCode = userCatCode.trim();
            final List userCatList = new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con, userCatCode);
            userCategoryVO = (CategoryVO) userCatList.get(0);
            
            if("Y".equals(userCategoryVO.getOutletsAllowed()))
            {
                // load the outlet dropdown
            	// load the outlet dropdown
                ArrayList outLetList=LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true);
                boolean flag5=true;
                for(int k=0;k<outLetList.size();k++)
                {
                	if(((ListValueVO)outLetList.get(k)).getValue().equals(requestVO.getData().getOutletCode()))
                	{
                		flag5=false;
                	}
                }
                if(flag5==true)
                {
                	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.OUTLET_CODE_DOES_NOT_EXIST);
                }
                channelUserVO.setOutletCode(requestVO.getData().getOutletCode());
                 final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
                 ArrayList suboutLetList=sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE);
                 boolean flag6=true;
                 for(int k=0;k<suboutLetList.size();k++)
                 {
                 	String suboutletCode=((ListValueVO)suboutLetList.get(k)).getValue();
                 	String []split=suboutletCode.split(":");
                 	if((split[1].equals(requestVO.getData().getOutletCode())))
                 	{
                 		if(split[0].equals(requestVO.getData().getSubOutletCode()))
                 		{
                 			flag6=false;
                 		}
                 	}
                 }
                 if(flag6==true)
                 {
                 	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.SUB_OUTLET_CODE_DOES_NOT_EXIST);
                 }
                 channelUserVO.setSubOutletCode(requestVO.getData().getSubOutletCode());
            }
            String parentMsisdn = channelUserVO.getParentMsisdn();
            String filteredParentMsisdn = null;
            if(!BTSLUtil.isNullString(parentMsisdn)){
                filteredParentMsisdn = PretupsBL.getFilteredMSISDN(parentMsisdn);
            }
            else
            {
            	filteredParentMsisdn = PretupsBL.getFilteredMSISDN(senderVO.getMsisdn());
            }
            parentChannelUserVO = channelUserDao.loadUsersDetails(con, filteredParentMsisdn, null, PretupsI.STATUS_NOTIN, status);
            
            final String newUserExtCode = requestVO.getData().getNewExternalcode();
            if (!BTSLUtil.isNullString(newUserExtCode)) {
                channelUserVO.setExternalCode(newUserExtCode);
            }
            else{
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTERNALCODE_BLANK_OR_LENGTH_EXCEEDS);
            }
            
            if(BTSLUtil.isContainsSpecialCharacters(newUserExtCode)) {
    			throw new BTSLBaseException(this, methodName,
    					PretupsErrorCodesI.EXTERNAL_CODE_SPCL_CHAR_NA);
    		}
            
            // External Code
            // Check given channel User MSISDN if it is not already existing ,
            // in request
            final HashMap mp = new HashMap();
            Msisdn [] msisdns=requestVO.getData().getMsisdn();
            Msisdn m = new Msisdn();
            List userPhoneList = new ArrayList<>();
            String primaryMsisdn = "";
            String randomPin;
            for(int i=0;i<msisdns.length;i++)
            {
            	m=msisdns[i];
            	if(m.getIsprimary().equals("Y"))
            		{
            		primaryMsisdn=m.getPhoneNo();
            		senderPin=m.getPin();
            		}
				if(!"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))){
             	            operatorUtili.validatePIN(m.getPin());
                 }

            	if(m.getPhoneNo()!=null)
            	userPhoneList.add(m.getPhoneNo());
            	 if (mp.containsKey(PretupsBL.getFilteredMSISDN(m.getPhoneNo()))) {
                     LOG.error(methodName, "Error: Duplicate entry of the MSISDN Number in the list");
                     throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DUPLICATE_MSISDN_IN_LIST);
                 } else {
                     mp.put(PretupsBL.getFilteredMSISDN(m.getPhoneNo()), PretupsBL.getFilteredMSISDN(m.getPhoneNo()));
                 }
            	
            }
            final String originalMsisdn = channelUserVO.getMsisdn();
            final String modifiedMsisdn = BTSLUtil.NullToString(primaryMsisdn);
            if(BTSLUtil.isNullString(modifiedMsisdn))
            {
            	channelUserVO.setMsisdn(originalMsisdn);
            }
            else
            {
            	channelUserVO.setMsisdn(modifiedMsisdn);
            }
           
            // User Name set
            boolean blank = true;
            final String modifiedUsername = requestVO.getData().getUserName();
            blank = BTSLUtil.isNullString(modifiedUsername);
            if (!blank) {
                channelUserVO.setUserName(modifiedUsername.toString().trim());
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.XML_ERROR_USER_NAME_BLANK);// Blank
                // User
                // Name
                // Exception
            }
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
            	if(BTSLUtil.isNullString(requestVO.getData().getFirstName()))
                {
                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.FIRST_NAME_BLANK);
                }
                channelUserVO.setFirstName(requestVO.getData().getFirstName());
                channelUserVO.setLastName(requestVO.getData().getLastName());
                if (!BTSLUtil.isNullString(requestVO.getData().getLastName())) {
                    channelUserVO.setUserName(requestVO.getData().getFirstName() + " " + requestVO.getData().getLastName());
                } else {
                    channelUserVO.setUserName(requestVO.getData().getFirstName());
                }
            } 
            if(!BTSLUtil.isNullString(requestVO.getData().getDesignation()) && !BTSLUtil.isValidInputField(requestVO.getData().getDesignation())){
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DESIGNATION_NOT_VALID);
            }
            if(!BTSLUtil.isNullString(requestVO.getData().getDesignation())){
                channelUserVO.setDesignation(requestVO.getData().getDesignation());
            }
            // User
            // Name
            // Mandatory
            // Value
            // short name set

            if(BTSLUtil.isNullString(requestVO.getData().getShortName()))
            {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SHORT_NAME_BLANK);
            }
            if(!BTSLUtil.isNullString(requestVO.getData().getShortName()) && !BTSLUtil.isValidName(requestVO.getData().getShortName())){
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.SHORT_NAME_INVALID);
            }

            // short name set
            final String modifiedUserShortname = requestVO.getData().getShortName();
            blank = BTSLUtil.isNullString(modifiedUserShortname);
            if (!blank) {
                channelUserVO.setShortName(modifiedUserShortname.toString().trim());
            }
            
            final String modifiedSubscriberCode = requestVO.getData().getSubscriberCode();
            blank = BTSLUtil.isNullString(modifiedSubscriberCode);
            if (!blank) {
                channelUserVO.setEmpCode(modifiedSubscriberCode.toString().trim());
            }

            // User prefix check
            String userPrifix = requestVO.getData().getUserNamePrefix();
            userPrifix = userPrifix.toUpperCase();
            ArrayList userNameList=LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true);
            boolean flag4=true;
            for(int k=0;k<userNameList.size();k++)
            {
            	if(((ListValueVO)userNameList.get(k)).getValue().equals(requestVO.getData().getUserNamePrefix()))
            	{
            		flag4=false;
            	}
            }
            if(flag4==true)
            {
            	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.USERNAME_TYPE_DOES_NOT_EXIST);
            }
            if (!userPrifix.equals(modifiesChannelUserVO.getUserNamePrefix())) {
                channelUserVO.setUserNamePrefix(userPrifix);
            }

            // set External Code

            // Set Contact Person , contact number , ssn , address1, address2,
            // city , state, country ,email id
            final String modifiedContactPerson = requestVO.getData().getContactPerson();
            blank = BTSLUtil.isNullString(modifiedContactPerson);
            if (!blank) {
                channelUserVO.setContactPerson(modifiedContactPerson.toString().trim());
            }

            final String modifiedContactNumber = requestVO.getData().getContactNumber();
            blank = BTSLUtil.isNullString(modifiedContactNumber);
            if (!blank) {
                channelUserVO.setContactNo(modifiedContactNumber.toString().trim());
            }

            final String modifiedSSN =requestVO.getData().getSsn();
            blank = BTSLUtil.isNullString(modifiedSSN);
            if (!blank) {
                channelUserVO.setSsn(modifiedSSN.toString().trim());
            }

            // Address1
            final String modifedAddress1 = requestVO.getData().getAddress1();
            blank = BTSLUtil.isNullString(modifedAddress1);
            if (!blank) {
                channelUserVO.setAddress1(modifedAddress1.toString().trim());
            }

            // Address2
            final String modifedAddress2 = requestVO.getData().getAddress2();
            blank = BTSLUtil.isNullString(modifedAddress2);
            if (!blank) {
                channelUserVO.setAddress2(modifedAddress2.toString().trim());
            }

            final String modifedCity = requestVO.getData().getCity();
            blank = BTSLUtil.isNullString(modifedCity);
            if (!blank) {
                channelUserVO.setCity(modifedCity.toString().trim());
            }

            // State
            final String modifedState = requestVO.getData().getState();
            blank = BTSLUtil.isNullString(modifedState);
            if (!blank) {
                channelUserVO.setState(modifedState.toString().trim());
            }

            // Country
            final String modifedCountry = requestVO.getData().getCountry();
            blank = BTSLUtil.isNullString(modifedCountry);
            if (!blank) {
                channelUserVO.setCountry(modifedCountry.toString().trim());
            }

            // EmailId
            final String modifedEmail = requestVO.getData().getEmailid();
            blank = BTSLUtil.isNullString(modifedEmail);
            boolean validEmail = false;
            if (!blank) {
                validEmail = BTSLUtil.validateEmailID(modifedEmail);
                if (validEmail) {
                    channelUserVO.setEmail(modifedEmail.toString().trim());
                }
                else
                {
                	throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.INVALID_EMAIL_MAPP);
                }
            }
            else
            {
                 	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.EXTSYS_REQ_EMAILID_NOTFOUND);
            }
            // other email check
            final String otherEmail = requestVO.getData().getOtherEmail();
            blank = BTSLUtil.isNullString(otherEmail);
            boolean validOEmail = false;
            if (!blank) {
            	validOEmail = BTSLUtil.validateEmailID(otherEmail);
                if (validOEmail) {
                    channelUserVO.setAlertEmail(otherEmail.toString().trim());
                }
                else
                {
                	throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.INVALID_EMAIL_MAPP);
                }
            }

            // WebLoginId
            final String modifedLoginId = requestVO.getData().getWebloginid();
            final String existingLoginId = (String) modifiesChannelUserVO.getLoginID();
            if (!BTSLUtil.isNullString(modifedLoginId) && !modifedLoginId.equals(existingLoginId)) {
                if (new UserDAO().isUserLoginExist(con, modifedLoginId, null)) {
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_LOGINID_ALREADY_EXIST);
                    // 11-MAR-2014
                } else {
                    channelUserVO.setLoginID(modifedLoginId.toString().trim());
                    // Ended Here
                }
            } else {
                channelUserVO.setLoginID(existingLoginId.toString().trim());
            }

            // Web Password
            String modifedPassword = requestVO.getData().getWebpassword();
            modifedPassword = BTSLUtil.decryptText(modifedPassword);
            if (!BTSLUtil.isNullString(modifedPassword)) {
                final Map errorMessageMap = operatorUtili.validatePassword(channelUserVO.getLoginID(), modifedPassword);
                if (null != errorMessageMap && errorMessageMap.size() > 0) {
                    Integer minLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH);
                    Integer maxLoginPwdLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH);
                    final String[] argsArray = { minLoginPwdLength.toString(), maxLoginPwdLength.toString()};
                    throw new BTSLBaseException("ModifyChannelUserg", methodName,
                            PretupsErrorCodesI.EXTSYS_REQ_WEBPASSWORD_INVALID_MODIFIED,argsArray);

                }
            }
            boolean isWebPasswordChanged = false;
            if (!BTSLUtil.isNullString(modifedPassword) && !BTSLUtil.encryptText(modifedPassword).equalsIgnoreCase(modifiesChannelUserVO.getPassword())) {
                isWebPasswordChanged = true;
                // Ended Here
            }
            
            // If in modify request, web password is not mentioned, then set the
            // previous one.
            if (BTSLUtil.isNullString(modifedPassword)) {
                modifedPassword = BTSLUtil.decryptText(channelUserVO.getPassword());
            }
            // while updating encrypt the password
            if(!"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
            	channelUserVO.setPassword(BTSLUtil.encryptText(modifedPassword)); 
            }else {
            	channelUserVO.setPassword(channelUserVO.getPassword());
            }

            // Set some use full parameter
            final Date currentDate = new Date();
            // prepare user phone list
           
            if(userPhoneList.size()>0)
            {
            	userPhoneList = prepareUserPhoneVOList(con, requestVO, modifiesChannelUserVO, currentDate, oldPhoneList, senderPin);
            }
            channelUserVO.setModifiedBy(senderVO.getUserID());
            channelUserVO.setModifiedOn(currentDate);
            if (channelUserVO.getMsisdn() != null) {
            	 boolean isUserCode=(Boolean) PreferenceCache.getSystemPreferenceValue(PretupsI.USER_CODE_REQUIRED);
                 if(isUserCode)
                 {
                 	channelUserVO.setUserCode(requestVO.getData().getUserCode());
                 }
                 else{
                 	channelUserVO.setUserCode(channelUserVO.getMsisdn());
                 }
            }
            String passValue = "";
            if (!BTSLUtil.isNullString(channelUserVO.getPassword())) {
                // modified by ashishT
                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    passValue = "********";
                } else {
                    passValue = BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(channelUserVO.getPassword()));
                }
            }
            channelUserVO.setShowPassword(passValue);
            if (!BTSLUtil.isNullString(modifedPassword) && (channelUserVO.getShowPassword().equals(BTSLUtil.getDefaultPasswordText(BTSLUtil.decryptText(channelUserVO.getPassword()))))) {
                channelUserVO.setPassword(channelUserVO.getPassword());
                if (channelUserVO.getPasswordModifiedOn() != null) {
                    channelUserVO.setPasswordModifiedOn(channelUserVO.getPasswordModifiedOn());
                } else {
                    channelUserVO.setPasswordModifiedOn(currentDate);
                }
                channelUserVO.setPasswordModifyFlag(false);
            } 
            else if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE)))
        	{
        		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PWD_GENERATE_ALLOW))).booleanValue() && "N".equals(PretupsI.NO)){
        			  channelUserVO.setPassword(channelUserVO.getPassword());
                      if (channelUserVO.getPasswordModifiedOn() != null) {
                          channelUserVO.setPasswordModifiedOn(channelUserVO.getPasswordModifiedOn());
                      } else {
                          channelUserVO.setPasswordModifiedOn(currentDate);
                      }
                      channelUserVO.setPasswordModifyFlag(false);
        		}
        	}
            else {
                // Change Done by ashishT
                if (!"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    // check if Last 'X' password exist or not in
                    // pin_password history table during
                    // modification time
                    boolean passwordExist = false;
                    passwordExist = userDAO.checkPasswordHistory(con, PretupsI.USER_PASSWORD_MANAGEMENT, channelUserVO.getUserID(), channelUserVO.getMsisdn(),
                                    BTSLUtil.encryptText(channelUserVO.getShowPassword()));
                    if (passwordExist) {
                        throw new BTSLBaseException(this, methodName, "user.modifypwd.error.newpasswordexistcheck", 0, new String[] { String
                                        .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()) }, "Detail");
                    }
                }
                // while updating encrypt the password
                String password = null;
                if (!BTSLUtil.isNullString(channelUserVO.getLoginID())) {
                    password = BTSLUtil.encryptText(channelUserVO.getShowPassword());
                }
                channelUserVO.setPassword(password);
                channelUserVO.setPasswordModifiedOn(currentDate);
                isWebPasswordChanged = true;
                channelUserVO.setPasswordModifyFlag(true);
            }
            final String userProfileId = channelUserVO.getUserProfileID();
            channelUserVO.setUserProfileID(userProfileId);
            // Employee Code
            final String empCode = requestVO.getData().getEmpcode();
            blank = BTSLUtil.isNullString(empCode);
            if (!blank) {
                channelUserVO.setEmpCode(empCode.toString().trim());
            }
            
            String inSuspend = requestVO.getData().getInsuspend();
            if(!BTSLUtil.isNullString(inSuspend)){
                if(inSuspend.equals(PretupsI.YES) || inSuspend.equals(PretupsI.NO)){
                    channelUserVO.setInSuspend(inSuspend);
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_INSUSPEND_INVALID);
                }    
            }else{
                channelUserVO.setInSuspend(modifiesChannelUserVO.getInSuspend());
            }    
            String outSuspend = requestVO.getData().getOutsuspend();
            if(!BTSLUtil.isNullString(outSuspend)){
                if(outSuspend.equals(PretupsI.YES) || outSuspend.equals(PretupsI.NO)){
                    channelUserVO.setOutSuspened(outSuspend);
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_OUTSUSPEND_INVALID);
                }        
            }else{
                channelUserVO.setOutSuspened(modifiesChannelUserVO.getOutSuspened());
            }   
            
            String company = requestVO.getData().getCompany();
            if(!BTSLUtil.isNullString(company)){
                channelUserVO.setCompany(company);
            }
            
            String fax = requestVO.getData().getFax();
            if(!BTSLUtil.isNullString(fax)){
                if(BTSLUtil.isValidNumber(fax)){
                    channelUserVO.setFax(fax);
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FAX_INVALID);
                }
            }
             
            channelUserVO.setLongitude(requestVO.getData().getLongitude() != null ? requestVO.getData().getLongitude()  : "");
            channelUserVO.setLatitude(requestVO.getData().getLatitude()  != null ? requestVO.getData().getLatitude() : "");
            ArrayList documentList=LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true);
            if(!(BTSLUtil.isNullOrEmptyList(documentList))&& !BTSLUtil.isNullString(requestVO.getData().getDocumentType()))
            {boolean flag2=true;
            for(int k=0;k<documentList.size();k++)
            {
            	if(((ListValueVO)documentList.get(k)).getValue().equals(requestVO.getData().getDocumentType()))
            	{
            		flag2=false;
            	}
            }
            if(flag2==true)
            {
            	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.DOCUMENT_TYPE_DOES_NOT_EXIST);
            }
            }
            
            channelUserVO.setDocumentType(requestVO.getData().getDocumentType()!= null ? requestVO.getData().getDocumentType() : "");
            channelUserVO.setDocumentNo(requestVO.getData().getDocumentNo() != null ? requestVO.getData().getDocumentNo()  : "");
            ArrayList paymentList=LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true);
            if(!BTSLUtil.isNullOrEmptyList(paymentList)&&!BTSLUtil.isNullString(requestVO.getData().getPaymentType()))
            {
            	ArrayList<String> payList=new ArrayList<>();
            for(int l=0;l<paymentList.size();l++)
            {
            	payList.add(((ListValueVO)paymentList.get(l)).getValue());
            }
            boolean flag3=false;
            String paymentTypes=requestVO.getData().getPaymentType();
            if(paymentTypes!=null)
            {
            String []payTypes=paymentTypes.split(",");
            for(int k=0;k<payTypes.length;k++)
            {
            	if(!(payList.contains(payTypes[k])))
            	{
            		flag3=true;
            		break;
            	}
            }
            if(flag3==true)
            {
            	throw new BTSLBaseException("AddChannelUser", methodName, PretupsErrorCodesI.PAYMENT_TYPE_DOES_NOT_EXIST);
            }
            }
            }
            channelUserVO.setPaymentTypes(requestVO.getData().getPaymentType()  != null ? requestVO.getData().getPaymentType()  : "");
            
            if (TypesI.YES.equals(userCategoryVO.getLowBalAlertAllow())) {
                final String delimiter = ";";
                final String allowforself = BTSLUtil.NullToString(requestVO.getData().getLowbalalertself()  );
                final String allowforparent = BTSLUtil.NullToString(requestVO.getData().getLowbalalertparent() );
                final String allowforOther = BTSLUtil.NullToString(requestVO.getData().getLowbalalertother() );
                final StringBuilder alerttype = new StringBuilder("");
                if (TypesI.YES.equals(allowforself)) {
                    alerttype.append(PretupsI.ALERT_TYPE_SELF);
                }
                if (TypesI.YES.equals(allowforparent)) {
                    alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
                    alerttype.append(PretupsI.ALERT_TYPE_PARENT);
                }
                if (TypesI.YES.equals(allowforOther)) {
                    alerttype.append("".equals(alerttype.toString()) ? "" : delimiter);
                    alerttype.append(PretupsI.ALERT_TYPE_OTHER);
                }

                if (!"".equals(alerttype.toString())) {
                    channelUserVO.setLowBalAlertAllow(TypesI.YES);
                    channelUserVO.setAlertType(alerttype.toString());
                } else {
                    channelUserVO.setLowBalAlertAllow(modifiesChannelUserVO.getLowBalAlertAllow());
                }

            }else {
                channelUserVO.setLowBalAlertAllow(modifiesChannelUserVO.getLowBalAlertAllow());
            }

            Date appointmentDate = null;
            if(!BTSLUtil.isNullString(requestVO.getData().getAppointmentdate()) ){
                try {
                	if(!BTSLUtil.isValidDatePattern(requestVO.getData().getAppointmentdate()))
                	{
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
                	}
                    appointmentDate = BTSLUtil.getDateFromDateString(requestVO.getData().getAppointmentdate());
                } catch (Exception e) {
                	LOG.error(methodName, "Exception " + e);
    				LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_APPOINTMENTDATE_INVALID);
                }
                channelUserVO.setAppointmentDate(appointmentDate);
            }
            
            final String allowedIPs = requestVO.getData().getAllowedip();
            if(!BTSLUtil.isNullString(allowedIPs)){
                String[] allowedIPAddress = allowedIPs.split(",");
                int allowIPAddress=allowedIPAddress.length;
                for(int i=0; i<allowIPAddress; i++){
                    String splitAllowedIP = allowedIPAddress[i];
                    if(!BTSLUtil.isValidateIpAddress(splitAllowedIP)){
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_IP_INVALID);
                    }
                }
                channelUserVO.setAllowedIps(allowedIPs);
            }
            
            final String allowedDays = requestVO.getData().getAlloweddays();
            if(!BTSLUtil.isNullString(allowedDays)){
                if(!BTSLUtil.isValidateAllowedDays(allowedDays)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ALLOWEDDAYS_INVALID);
                }
                channelUserVO.setAllowedDays(allowedDays);
            }
            else {
            	channelUserVO.setAllowedDays(null);
            }
            
            final String fromTime = requestVO.getData().getAllowedTimeFrom();
            final String toTime = requestVO.getData().getAllowedTimeTo();

            if(!BTSLUtil.isNullString(fromTime)){
                if(!BTSLUtil.isValidateAllowedTime(fromTime)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_FROMTIME_INVALID);
                }
                channelUserVO.setFromTime(fromTime);
            }

            if(!BTSLUtil.isNullString(toTime)){
                if(!BTSLUtil.isValidateAllowedTime(toTime)){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_TOTIME_INVALID);
                }
                channelUserVO.setToTime(toTime);
            }
            if (!BTSLUtil.isNullString(channelUserVO.getFromTime()) && !BTSLUtil.isNullString(channelUserVO.getToTime())) {
            if (channelUserVO.getFromTime().equals(channelUserVO.getToTime())) {
            	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TIME_RANGE);
            }            
            }
            boolean rsaRequired = false;
            rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
            channelUserVO.setRsaRequired(rsaRequired);
           int check = 4;
           
            ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
            ArrayList commissionProfileList=userWebDAO.loadCommisionProfileListByCategoryIDandGeography(con, channelUserVO.getCategoryCode(), senderVO.getNetworkID(),null);

            final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
            // load the User Grade dropdown
            ArrayList gradelist=categoryGradeDAO.loadGradeList(con, channelUserVO.getCategoryCode());

            // load the Transfer Profile dropdown
            final TransferProfileDAO profileDAO = new TransferProfileDAO();
            ArrayList transferprofilelist=profileDAO.loadTransferProfileByCategoryID(con, senderVO.getNetworkID(), channelUserVO.getCategoryCode(),
                            PretupsI.PARENT_PROFILE_ID_USER);
            // load the Transfer Rule Type at User level
            ArrayList transferRuleTypeList =null;
            final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, senderVO.getNetworkID(), channelUserVO.getCategoryCode())).booleanValue();
            if (isTrfRuleTypeAllow) {
            	transferRuleTypeList=(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
            }
            
            ArrayList LmsProfileList=null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
                LmsProfileList=channelUserWebDAO.getLmsProfileList(con, senderVO.getNetworkID());
            }
            UserRolesDAO userRolesDAO1 = new UserRolesDAO();
            boolean flag = true;
//            if (((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), userCatCode))
//                    .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()||(!(((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), userCatCode))
//                            .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) && userRolesDAO1.isUserRoleCodeAssociated(con,senderVO.getUserID(),"ASSCUSR"))) {
            if (((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), userCatCode))
                    .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()
                    ||(!(((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), userCatCode))
                            .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) 
                    		&& userRolesDAO1.isUserRoleCodeAssociated(con,senderVO.getUserID(),"ASSCUSR"))) {
            	 String userGrade = requestVO.getData().getUsergrade();
            	           if(!channelUserVO.getStatus().equals(PretupsI.USER_STATUS_NEW) ) {   	 
                 if(!BTSLUtil.isNullString(userGrade)){
                     String userGradeCode = null;
                     GradeVO gradeVO = new GradeVO();
                     List userGradeList = new CategoryGradeDAO().loadGradeList(con, userCatCode);
                     List<String> gradeCodeList = new ArrayList<String>();
                     int userGradeLists=userGradeList.size();
                     for (int i = 0; i <userGradeLists ; i++) {
                         gradeVO = (GradeVO) userGradeList.get(i);
                         gradeCodeList.add(gradeVO.getGradeCode());
                     }
                          
                     if(gradeCodeList.contains(userGrade)){
                         userGradeCode = userGrade.trim();
                         channelUserVO.setUserGrade(userGradeCode);
                         --check;
                     }else if(!gradeCodeList.contains(userGrade)){
                         throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
                     }
                 }
                 else {

                     throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GRADE_INVALID);
                 }
            	
            	String commissionProfileID = requestVO.getData().getCommissionProfileID();
                if(!BTSLUtil.isNullString(commissionProfileID))
                {

            		flag=true;
            		for(int i=0;i<commissionProfileList.size();i++)
            		{
            			if(((CommissionProfileSetVO)commissionProfileList.get(i)).getCommProfileSetId().equals(commissionProfileID))
            			{
            				flag=false;
            			}
            		}
            		if(flag==true)
            		{
            			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
            		}
            	
                	channelUserVO.setCommissionProfileSetID(commissionProfileID);
                	--check;
                }
                else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.COMMISSION_SET_ID_INVALID);
                }
                
                String transferProfile = requestVO.getData().getTransferProfile();
                if(!BTSLUtil.isNullString(transferProfile))
                {
            		flag=true;
        		for(int i=0;i<transferprofilelist.size();i++)
        		{
        			if(((ListValueVO)transferprofilelist.get(i)).getValue().equals(transferProfile))
        			{
        				flag=false;
        			}
        		}
        		if(flag==true)
        		{
        			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST,new String[]{channelUserVO.getUserName()});
        		}
            	
                	channelUserVO.setTransferProfileID(transferProfile);
                	--check;
                }
                else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.CHANNEL_TRANSFER_PROFILE_NOT_EXIST,new String[]{channelUserVO.getUserName()});
                }
                
                String transferRuleType = requestVO.getData().getTransferRuleType();
                if(isTrfRuleTypeAllow)
                {
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW))).booleanValue()) {
                if(!BTSLUtil.isNullString(transferRuleType))
                {
            		flag=true;
        		for(int i=0;i<transferRuleTypeList.size();i++)
        		{
        			if(((ListValueVO)transferRuleTypeList.get(i)).getValue().equals(transferRuleType))
        			{
        				flag=false;
        			}
        		}
        		if(flag==true)
        		{
        			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NOT_EXIST);
        		}
            	
                		--check;
                		channelUserVO.setTrannferRuleTypeId(transferRuleType);
                	}
                else
                {
                	throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TRANSFER_RULE_TYPE_NOT_EXIST);	
                }
                }
                }
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
            		
            		String controlGroupRequired = Constants.getProperty("LMS_CONTROL_GROUP_REQUIRED");
        			if(controlGroupRequired == null || controlGroupRequired == ""){
        				controlGroupRequired="Y";
        			}
        			if("Y".equals(controlGroupRequired)) {
        				channelUserVO.setControlGroup(requestVO.getData().getControlGroup());
        			}
            		if(BTSLUtil.isNullString(requestVO.getData().getLmsProfileId()))
                	{
                		throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
                	}
            		else{

                		flag=true;
            		for(int i=0;i<LmsProfileList.size();i++)
            		{
            			if(((ListValueVO)LmsProfileList.get(i)).getValue().equals(requestVO.getData().getLmsProfileId()))
            			{
            				flag=false;
            			}
            		}
            		if(flag==true)
            		{
            			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.LMS_SETID_NOT_FOUND);
            		}
                	
            		}
                    channelUserVO.setLmsProfile(requestVO.getData().getLmsProfileId());
                }
               
            }else {
            	String transferRuleType = requestVO.getData().getTransferRuleType();
            	channelUserVO.setTrannferRuleTypeId(transferRuleType);
            }
            
            
        
        }    
            
            
            
            GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
            String geocode=requestVO.getData().getGeographyCode();
            if (!BTSLUtil.isNullString(geocode)){    
                // logic to validate the passed geocode
                GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
                boolean isValidGeoCode=false;
                // check for other level (SE and Retailer)
                if(!senderVO.getUserID().equals(parentChannelUserVO.getUserID())) {
                	final ArrayList parentUserGeographyList = geographyDAO.loadUserGeographyList(con, parentChannelUserVO.getUserID(), senderVO.getNetworkID());
                	 UserGeographiesVO geographyVO = null;
                     if (parentUserGeographyList != null && parentUserGeographyList.size() > 0) {
                         for (int i = 0, j = parentUserGeographyList.size(); i < j; i++) {
                             geographyVO = (UserGeographiesVO) parentUserGeographyList.get(i);
                             if (geographyVO.getGraphDomainCode().equals(parentChannelUserVO.getGeographicalCode())) {
                                 break;
                             }
                         }
                         
                         if (geographyVO.getGraphDomainType().equals(channelUserVO.getCategoryVO().getGrphDomainType())) {
                        	 	isValidGeoCode=true;
                         		defaultGeoCode=geocode;
                         } else if ((geographyVO.getGraphDomainSequenceNumber() + 1) == channelUserVO.getCategoryVO().getGrphDomainSequenceNo()){
                        	 GeographicalDomainWebDAO geographicalDomainWebDAO=new GeographicalDomainWebDAO();
                         	 List geographyList = geographicalDomainWebDAO.loadGeographyList(con, senderVO.getNetworkID(), geographyVO.getGraphDomainCode(), "%");
                             List geoList1 = new ArrayList<>();
                         	for(int i=0;i<geographyList.size();i++)
                             {
                            	 geoList1.add(((UserGeographiesVO)geographyList.get(i)).getGraphDomainCode());
                             }
                             if(!(geoList1.contains(geocode)))
                             {
                             	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
                             }
                             isValidGeoCode=true;
                             defaultGeoCode=geocode;
                         }
                     }
                }else {
                	 ArrayList<UserGeographiesVO> userGeoList=geographicalDomainDAO.loadUserGeographyList(con, senderVO.getUserID(), senderVO.getNetworkID());
                 if (senderVO.getCategoryVO().getGrphDomainType().equals(channelUserVO.getCategoryVO().getGrphDomainType())) {
                	
                    List geoList = new ArrayList<>();
                    for(int i=0;i<userGeoList.size();i++)
                    {
                    	geoList.add(((UserGeographiesVO)userGeoList.get(i)).getGraphDomainCode());
                    }
                    if(!(geoList.contains(geocode)))
                    {
                    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
                    }
                    isValidGeoCode=true;
                    defaultGeoCode=geocode;
                }
                 else if ((senderVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == channelUserVO.getCategoryVO().getGrphDomainSequenceNo()) {
                	GeographicalDomainWebDAO geographicalDomainWebDAO=new GeographicalDomainWebDAO();
                	List geographyList = geographicalDomainWebDAO.loadGeographyList(con, senderVO.getNetworkID(), parentChannelUserVO.getGeographicalCode(), "%");
                    List geoList1 = new ArrayList<>();
                	for(int i=0;i<geographyList.size();i++)
                    {
                   	 geoList1.add(((UserGeographiesVO)geographyList.get(i)).getGraphDomainCode());
                    }
                    if(!(geoList1.contains(geocode)))
                    {
                    	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
                    }
                    isValidGeoCode=true;
                    defaultGeoCode=geocode;
                }else{
                   
                	 String parentId = channelUserVO.getParentID();
                     if (PretupsI.ROOT_PARENT_ID.equals(parentId)) {
                         parentId = channelUserVO.getUserID();
                     }
                     final ArrayList parentUserGeographyList = geographyDAO.loadUserGeographyList(con, parentId, senderVO.getNetworkID());
                     final String geoCode1 = ((UserGeographiesVO) parentUserGeographyList.get(0)).getGraphDomainCode();
                     List list = geographyDAO.loadGeoDomainCodeHeirarchy(con, channelUserVO.getCategoryVO().getGrphDomainType(), geoCode1, true);
                     if (list != null) {
                         final ArrayList finalList = new ArrayList();
                         UserGeographiesVO geographyVO = null;
                         GeographicalDomainVO geographicalDomainVO = new GeographicalDomainVO();
                         for (int i = 0, j = list.size(); i < j; i++) {
                             geographyVO = new UserGeographiesVO();
                             geographicalDomainVO = (GeographicalDomainVO) list.get(i);
                             geographyVO.setGraphDomainCode(geographicalDomainVO.getGrphDomainCode());
                             finalList.add(geographyVO);
                         }
                         List geoList = finalList;
                         List geoList1 = new ArrayList<>();
                         LogFactory.printLog(methodName, "top level hirearchy = "+geocode, LOG);
                         for(int i=0;i<geoList.size();i++)
                         {
                        	 geoList1.add(((UserGeographiesVO)geoList.get(i)).getGraphDomainCode());
                         }
                         if(!(geoList1.contains(geocode)))
                         {
                         	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
                         }
                     }
                 	
                    if(!geographyDAO.isGeographicalDomainExist(con, geocode, true)){
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY); 
                    }  
                    defaultGeoCode=geocode;
                    isValidGeoCode=true;
                }
                }
                if(!isValidGeoCode){
                    response.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
                  
                    throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_GEOGRAPHY);
                }
                LogFactory.printLog(methodName, "Passed GeoCode = "+defaultGeoCode, LOG);
            }
            
            if(!BTSLUtil.isNullString(defaultGeoCode)){
                channelUserVO.setGeographicalCode(defaultGeoCode);
                final ArrayList geoList = new ArrayList();
                final UserGeographiesVO userGeographiesVO = new UserGeographiesVO();
                userGeographiesVO.setUserId(channelUserVO.getUserID());
                userGeographiesVO.setGraphDomainCode(channelUserVO.getGeographicalCode());
                LogFactory.printLog(methodName, "channelUserVO.getGeographicalCode() >> " + channelUserVO.getGeographicalCode(), LOG);
                geoList.add(userGeographiesVO);
                int deleteUserGeo=new UserGeographiesDAO().deleteUserGeographies(con, channelUserVO.getUserID());
                if (deleteUserGeo <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:deleteUserGeo <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
                final int addUserGeo = new UserGeographiesDAO().addUserGeographyList(con, geoList);
                if (addUserGeo <= 0) {
                    con.rollback();
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:addUserGeo <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_REGISTRATION_FAILED);
                }
            }
            
            String services = requestVO.getData().getServices();
            final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
            if(!BTSLUtil.isNullString(services))
            {
            ListValueVO listValueVO = null;
            List serviceList = null;
            String networkCode = requestVO.getData().getExtnwcode();
            try {
                serviceList = servicesTypeDAO.loadServicesList(con, networkCode, PretupsI.C2S_MODULE, userCatCode, false);
                if(!BTSLUtil.isNullString(services)){
                    List<String> serviceTypeList = new ArrayList<String>();
                    int serviceListsizes=serviceList.size();
                    for (int i = 0; i < serviceListsizes; i++) {
                        listValueVO = (ListValueVO) serviceList.get(i);
                        serviceTypeList.add(listValueVO.getValue());
                    }
                    boolean isServiceValid = true;
                    final String[] givenService = services.split(",");
                    int givenServices=givenService.length;
                    for (int i = 0; i < givenServices; i++) {
                        if(!serviceTypeList.contains(givenService[i])){
                            isServiceValid = false;
                        }
                    }
                    if(isServiceValid){
                        servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
                        servicesTypeDAO.addUserServicesList(con, channelUserVO.getUserID(), givenService, PretupsI.YES);
                    }else{
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_INVALID);
                    }
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
            }
            } else { // delete unchecked services. if no services selected.
                servicesTypeDAO.deleteUserServices(con, channelUserVO.getUserID());
            }
            
            
            
            String groupRole = requestVO.getData().getGrouprole();
            final UserRolesDAO userRolesDAO = new UserRolesDAO();
            if(!BTSLUtil.isNullString(groupRole))
            {
            	if (PretupsI.YES.equalsIgnoreCase(userCategoryVO.getWebInterfaceAllowed())) {
                
                

                if(!BTSLUtil.isNullString(groupRole)){
                    Map rolesMap = userRolesDAO.loadRolesListByGroupRole(con, channelUserVO.getCategoryCode(), requestVO.getData().getRoleType());
                    Set rolesKeys = rolesMap.keySet();
                    List<String> rolesListNew=new ArrayList<String>();
                    Iterator keyiter = rolesKeys.iterator();
                    ArrayList grouprolesList = new ArrayList<>();
                    while(keyiter.hasNext()){
                        String rolename=(String)keyiter.next();
                        List rolesVOList=(List)rolesMap.get(rolename);
                        rolesListNew=new ArrayList();
                        Iterator i=rolesVOList.iterator();
                        while(i.hasNext()){
                            UserRolesVO rolesVO=(UserRolesVO)i.next();
                            if("Y".equalsIgnoreCase(rolesVO.getStatus())){
                                rolesListNew.add(rolesVO.getRoleCode());
                                grouprolesList.add(rolesVO.getRoleCode());
                            }
                          }
                        
                    }
                    
                    if(!rolesListNew.isEmpty()){
                    	String [] groupRoles = groupRole.split(",");
                    	String[] roles = new String[groupRoles.length];
                    	for(int i=0;i<groupRoles.length;i++)
                    	{
                    		if(grouprolesList.contains(groupRoles[i]))
                    		{
                    			roles[i] = groupRoles[i];
                    		}
                    		else{
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_GROUPROLE_INVALID);
                              }
                    	}
                    	    userRolesDAO.deleteUserRoles(con, channelUserVO.getUserID());
                            int userRoles=userRolesDAO.addUserRolesList(con, channelUserVO.getUserID(), roles);
                            if (userRoles <= 0) {
                                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
                            }
                        
                      }
                }
            
        }
            } else { // Delete roles, if unchecked. 
            	userRolesDAO.deleteUserRoles(con, channelUserVO.getUserID());
            }
            String voucherType = requestVO.getData().getVoucherTypes();
            if(!(BTSLUtil.isNullString(voucherType)))
            { 
            	String []voucherTypes = voucherType.split(",");
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED))).booleanValue() && ((Boolean)PreferenceCache.getControlPreference(PreferenceI.CHNLUSR_VOUCHER_CATGRY_ALLWD, channelUserVO.getNetworkID(), userCatCode)).booleanValue())
            {
            	if (voucherTypes != null && voucherTypes.length > 0) {
                
                VomsProductDAO voucherDAO = new VomsProductDAO();
                
				ArrayList <ListValueVO>voucherList = new ArrayList();
                voucherList = voucherDAO.loadVoucherTypeList(con);
                ArrayList voucTypes = new ArrayList<>();
                for(int i=0;i<voucherList.size();i++)
                {
                	voucTypes.add(voucherList.get(i).getValue());
                	
                }
                for(int i=0;i<voucherTypes.length;i++)
            	{
            		if(!(voucTypes.contains(voucherTypes[i])))
            		{
            			throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_TYPE_INVALID);
            		}
            	}
                userWebDAO.deleteUserVoucherTypes(con, channelUserVO.getUserID());
                int userVoucherTypeCount = voucherDAO.addUserVoucherTypeList(con, channelUserVO.getUserID(), voucherTypes, PretupsI.YES);
                if (userVoucherTypeCount <= 0) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        LOG.errorTrace(methodName, e);
                    }
                    LOG.error("addUserInfo", "Error: while Inserting User voucher type Info");
                    throw new BTSLBaseException(this, "addUserInfo", "error.general.processing");
                }
            	}
            }
            } else {
            	// delete the vouchertypes from database.
            	 userWebDAO.deleteUserVoucherTypes(con, channelUserVO.getUserID());
            }
            // insert in to user table
            if(BTSLUtil.isNullString(requestVO.getData().getAlloweddays())) {
            	channelUserVO.setAllowedDays(null);
            }
            final String allowedIP = requestVO.getData().getAllowedip();
            if(!BTSLUtil.isNullString(allowedIP)){
                String[] allowedIPAddress = allowedIP.split(",");
                int allowIPAddress=allowedIPAddress.length;
                for(int i=0; i<allowIPAddress; i++){
                    String splitAllowedIP = allowedIPAddress[i];
                    if(!BTSLUtil.isValidateIpAddress(splitAllowedIP)){
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_IP_INVALID);
                    }
                }
                channelUserVO.setAllowedIps(allowedIPs);
            }
            else {
            	channelUserVO.setAllowedIps(null);
            }
            channelUserVO.setAuthTypeAllowed(requestVO.getData().getAuthTypeAllowed());
            final int userCount = new UserDAO().updateUser(con, channelUserVO);
            if (userCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                    "Exception:userCount <=0 ");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
            }
            
            // insert data into channel users table
            if (((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), userCatCode))
                    .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()||(!(((Integer) PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, senderVO.getNetworkID(), userCatCode))
                            .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) && userRolesDAO1.isUserRoleCodeAssociated(con,senderVO.getUserID(),"ASSCUSR"))) {
        int updateChannelCount = channelUserWebDAO.updateChannelUserApprovalInfo(con, channelUserVO);

        if (updateChannelCount <= 0) {
            con.rollback();
            LOG.error(methodName, "Error: while Updating Channel User For Approval One");
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }
        }else{
            final int userChannelCount = channelUserDao.updateChannelUserInfo(con, channelUserVO);
            if (userChannelCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                    "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
            }
    }
            if (userPhoneList != null && !userPhoneList.isEmpty()) {
                final int phoneCount = userDAO.updateInsertDeleteUserPhoneList(con, (ArrayList)userPhoneList);
                if (phoneCount <= 0) {
                    try {
                        con.rollback();
                    } catch (SQLException e) {
                        LOG.errorTrace(methodName, e);
                    }
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, methodName, "", "", "",
                        "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERP_USER_REGISTRATION_FAILED);
                }
            }
            
            
            con.commit();
            String []messageArguments=null;
            final String arr[] = { channelUserVO.getUserName(), null,senderPin };
                UserPhoneVO oldUserPhoneVO = null;
                UserPhoneVO newUserPhoneVO = null;
                String primaryNoPin = null;
                BTSLMessages sendbtslMessage = null;
                final boolean pinFlag = false;
                boolean primaryNoPinFlag = false;
                Locale localeMsisdn = null;
                PushMessage pushMessage = null;
                // Email for pin & password
                String subject = null;
                EmailSendToUser emailSendToUser = null;
                final String tmpMsisdn = channelUserVO.getMsisdn();
                ;

                if (oldPhoneList != null) {
                    for (int i = 0, j = oldPhoneList.size(); i < j; i++) {
                        sendbtslMessage = null;
                        oldUserPhoneVO = (UserPhoneVO) oldPhoneList.get(i);
                        for (int k = 0, l = userPhoneList.size(); k < l; k++) {
                            newUserPhoneVO = (UserPhoneVO) userPhoneList.get(k);
                            if (!BTSLUtil.isNullString(newUserPhoneVO.getMsisdn()) && newUserPhoneVO.getMsisdn().equals(oldUserPhoneVO.getMsisdn())) {
                                if (TypesI.YES.equals(newUserPhoneVO.getPrimaryNumber())) {
                                    if (!(newUserPhoneVO.getShowSmsPin().equals(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(oldUserPhoneVO.getSmsPin()))))) {// primary
                                        // no
                                        // pin
                                        // change
                                        primaryNoPin = newUserPhoneVO.getShowSmsPin();// primary
                                        // no
                                        // pin
                                        // change
                                        primaryNoPinFlag = true;
                                    }
                                } else if (!(newUserPhoneVO.getShowSmsPin().equals(BTSLUtil
                                                .getDefaultPasswordNumeric(BTSLUtil.decryptText(oldUserPhoneVO.getSmsPin()))))) {// if
                                    // pin
                                    // change
                                    localeMsisdn = new Locale(newUserPhoneVO.getPhoneLanguage(), newUserPhoneVO.getCountry());

                                    // for Zebra and Tango by Sanjeew
                                    // date 11/07/07
                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PTUPS_MOBQUTY_MERGD))).booleanValue() && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PAYMENT_METHOD))).booleanValue() && PretupsI.SELECT_CHECKBOX
                                                    .equals(channelUserVO.getMcommerceServiceAllow())) {
                                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY,
                                                        new String[] { newUserPhoneVO.getShowSmsPin(), "" });
                                    } else {
                                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { newUserPhoneVO.getShowSmsPin() });
                                    }
                                   pushMessage = new PushMessage(newUserPhoneVO.getMsisdn(), sendbtslMessage, "", "", localeMsisdn, channelUserVO.getNetworkID(),
                                                    "SMS will be delivered shortly thankyou");
                                    pushMessage.push();
                                    // Email for pin & password- email
                                    // send
                                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                                        channelUserVO.setMsisdn(newUserPhoneVO.getMsisdn());
                                        subject = BTSLUtil.getMessage(locale, "subject.user.regmsidn.massage.modify",
                                                        new String[] { newUserPhoneVO.getMsisdn() });
                                        emailSendToUser = new EmailSendToUser(subject, sendbtslMessage, locale, channelUserVO.getNetworkID(),
                                                        "Email will be delivered shortly", channelUserVO, senderVO);
                                        emailSendToUser.sendMail();
                                        channelUserVO.setMsisdn(tmpMsisdn);
                                    }
                                }
                                // comment break for sending messages to
                                // all msisdn
                                // break;
                            }// end of
                             // if(newUserPhoneVO.getMsisdn().equals(oldUserPhoneVO.getMsisdn()))
                        }// end of for(int k=0, l=newMsisdnList.size();
                         // k<l;k++)
                    }// end of for(int i=0, j=oldMsisdnList.size(); i<j;
                     // i++)
                }
               final String msg[] = new String[3];
                sendbtslMessage = null;
                if (!BTSLUtil.isNullString(channelUserVO.getLoginID()) && !BTSLUtil.isNullString(existingLoginId)) {
                    // only web password change
                    if (isWebPasswordChanged && !primaryNoPinFlag && channelUserVO.getLoginID().equals(existingLoginId)) {
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY, new String[] { channelUserVO.getShowPassword() });
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PWD_MODIFY);
                        messageArguments=new String[] { channelUserVO.getShowPassword() };
                    } else if (isWebPasswordChanged && primaryNoPinFlag && modifedLoginId.equals(existingLoginId)) {
                        msg[0] = channelUserVO.getShowPassword();
                        msg[1] = primaryNoPin;
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY, msg);
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PWD_AND_PIN_MODIFY);
                        messageArguments=msg;
                    }
                    // web loginid and web password and primary no pin
                    // change
                    else if (isWebPasswordChanged && primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                        msg[0] = channelUserVO.getLoginID();
                        msg[1] = channelUserVO.getShowPassword();
                        msg[2] = primaryNoPin;
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY, msg);
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_AND_PIN_MODIFY);
                        messageArguments=msg;
                    }
                    // only login id change
                    else if (!isWebPasswordChanged && !primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                        msg[0] = channelUserVO.getLoginID();
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY, msg);
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_MODIFY);
                        messageArguments=msg;
                    }
                    // only login id and web password change
                    else if (isWebPasswordChanged && !primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                        msg[0] = channelUserVO.getLoginID();
                        msg[1] = channelUserVO.getShowPassword();
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY, msg);
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PWD_MODIFY);
                        messageArguments=msg;
                    }
                    // only login id and pin change
                    else if (!isWebPasswordChanged && primaryNoPinFlag && !channelUserVO.getLoginID().equals(existingLoginId)) {
                        msg[0] = channelUserVO.getLoginID();
                        msg[1] = primaryNoPin;
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY, msg);
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_LOGIN_AND_PIN_MODIFY);
                        messageArguments=msg;
                    }
                    // only primary no pin change.
                    else if (!isWebPasswordChanged && primaryNoPinFlag &&channelUserVO.getLoginID().equals(existingLoginId)) {
                        msg[0] = primaryNoPin;
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, msg);
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY);
                        messageArguments=msg;
                    }
                }// if(!BTSLUtil.isNullString(theForm.getWebLoginID())
                 // &&
                 // !BTSLUtil.isNullString(theForm.getOldWebLoginID()))
                else {
                    if (primaryNoPinFlag) {
                        sendbtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY, new String[] { primaryNoPin });
                        response.setMessageCode(PretupsErrorCodesI.CHNL_USER_PIN_MODIFY);
                        messageArguments=new String[] { primaryNoPin };
                    }
                }
                // Send SMS
           
                if (sendbtslMessage != null) {
                    
                    PushMessage pushMessage1 = new PushMessage(channelUserVO.getMsisdn(), sendbtslMessage, "", "", locale, channelUserVO.getNetworkID(),
                                    "SMS will be delivered shortly thanks");
                    pushMessage1.push();
                    // Email for pin & password
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(channelUserVO.getEmail())) {
                        String subject1 = BTSLUtil.getMessage(locale, "user.addchanneluser.updatesuccessmessage", arr);
                        EmailSendToUser emailSendToUser1 = new EmailSendToUser(subject1, sendbtslMessage, locale, channelUserVO.getNetworkID(), "Email will be delivered shortly",
                                        channelUserVO, senderVO);
                        emailSendToUser1.sendMail();
                    }
                }
            
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REALTIME_OTF_MSGS))).booleanValue() &&(((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,channelUserVO.getNetworkID()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,channelUserVO.getNetworkID())) && (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL,channelUserVO.getNetworkID(),userCatCode)).intValue()==0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue())  )){
            	TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
            	tbcm.loadCommissionProfileDetailsForOTFMessages(con,channelUserVO);
            }
            ChannelUserLog.log("MODCHNLUSR", channelUserVO, senderVO, true, null);
            response.setStatus(200);
         
			if(BTSLUtil.isNullString(response.getMessageCode()))
			{
				response.setMessageCode(PretupsErrorCodesI.CHANNEL_USER_UPDATE);
			}
			response.setMessage("User "+channelUserVO.getUserName()+" "+RestAPIStringParser.getMessage(locale,response.getMessageCode(),messageArguments));
        } catch (BTSLBaseException be) {
            response.setStatus(400);
            if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
            LOG.errorTrace(methodName, be);
            if (be.isKey()) {
                response.setMessageCode(be.getMessageKey());
                response.setMessage(RestAPIStringParser.getMessage(locale, be.getMessageKey(), be.getArgs()));
            } else {
                response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return response;
            }
        } catch (Exception e) {
        	response.setStatus(400);
        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, methodName, "", "", "",
                "Exception:" + e.getMessage());
            response.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return response;
        } finally {
            channelUserDao = null;
            userDAO = null;
            channelUserVO = null;
            userCategoryVO = null;

            if(mcomCon != null){mcomCon.close("ModifyChannelUserController#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
        }
		return response;
    }

    
    private List prepareUserPhoneVOList(Connection con, ModifyChannelUserRequestVO requestVO, ChannelUserVO channelUserVO, Date currentDate,
    		ArrayList oldPhoneList, String senderPin) throws Exception {
        final String methodName = "prepareUserPhoneVOList";
        LogFactory.printLog(methodName, "Entered oldPhoneList.size()=" + oldPhoneList.size() 
                , LOG);

        final List <UserPhoneVO>phoneList = new ArrayList<UserPhoneVO>();
        final List <Msisdn>newMsisdnList=new ArrayList<Msisdn>();
        Msisdn [] msisdns=requestVO.getData().getMsisdn();
        Msisdn m = new Msisdn();
        for(int i=0;i<msisdns.length;i++)
        {
        	m=msisdns[i];
        	newMsisdnList.add(m);
        }
        NetworkPrefixVO networkPrefixVO = null;
        Msisdn msisdn = null;
        String stkProfile = null;
        final String oldUserPhoneID = null;
        final List stkProfileList = userDAO.loadPhoneProfileList(con, channelUserVO.getCategoryCode());
        if (stkProfileList != null) {
            final ListValueVO listValueVO = (ListValueVO) stkProfileList.get(0);
            stkProfile = listValueVO.getValue();
        }
        if (newMsisdnList != null && !newMsisdnList.isEmpty()) {
        	
        	boolean[] msisdnDelete = new boolean[oldPhoneList.size()]; 
            UserPhoneVO phoneVO = null;
            UserPhoneVO oldPhoneVO = null;
            int i=0;
            int j=0;
         
            for (i = 0, j = newMsisdnList.size(); i < j; i++) {
            	if(!(newMsisdnList.get(i).getPin().equals(newMsisdnList.get(i).getConfirmPin()))) {
            		throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.C2S_PIN_NEWCONFIRMNOTSAME);
            	}
                phoneVO = new UserPhoneVO();
                oldPhoneVO= null;
                msisdn = (Msisdn) newMsisdnList.get(i);
                phoneVO.setMsisdn(msisdn.getPhoneNo());
                phoneVO.setPinModifyFlag(true);
                phoneVO.setPhoneProfile(stkProfile);
                phoneVO.setShowSmsPin(newMsisdnList.get(i).getPin());
                
                if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {

                    if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                    	for(int k=0;k<oldPhoneList.size();k++) {
                    		UserPhoneVO tempOldPhoneVO=(UserPhoneVO) oldPhoneList.get(k);
                    		if (!BTSLUtil.isNullString(tempOldPhoneVO.getMsisdn()) && phoneVO.getMsisdn().equals(tempOldPhoneVO.getMsisdn())) {
                    			msisdnDelete[k]=true;
                    			oldPhoneVO=(UserPhoneVO) oldPhoneList.get(k);
                    			if (!(phoneVO.getShowSmsPin().equals(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(oldPhoneVO.getSmsPin()))))) {
                                    phoneVO.setPinModifyFlag(true);
                                } else {
                                    phoneVO.setPinModifyFlag(false);
                                }
                                break;
                    		}
                    	}
//                        if (i < oldPhoneList.size()) {
//                            oldPhoneVO = (UserPhoneVO) oldPhoneList.get(oldPhoneList.size() - (i + 1));
//                        }
                    }
                    phoneVO.setMsisdn(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()));
                    if (oldPhoneVO != null) {
                        phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
//                        phoneVO.setPinModifyFlag(false);
                        phoneVO.setOperationType("U");
                        
                        if (phoneVO.isPinModifyFlag() || phoneVO.getPinModifiedOn() == null) {
                            phoneVO.setPinModifiedOn(currentDate);
                        }
//                        phoneVO.setPinModifiedOn(oldPhoneVO.getPinModifiedOn());
                        phoneVO.setPinRequired(oldPhoneVO.getPinRequired());
                        if(!"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                        	phoneVO.setSmsPin(BTSLUtil.encryptText(msisdn.getPin())); 
                        }else {
                        	phoneVO.setSmsPin(msisdn.getPin());
                        }
                        phoneVO.setDescription(msisdn.getDescription());
                        if(msisdn.getStkProfile()==null)
                        {
                        	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                        }
                        else if(!(msisdn.getStkProfile().equals(stkProfile)))
                        {
                        	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                        }
                        phoneVO.setPhoneProfile(msisdn.getStkProfile());
                        phoneVO.setIdGenerate(false);
                    } else if (oldPhoneVO==null) {
                        phoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                        phoneVO.setOperationType("I");
                        phoneVO.setPinModifyFlag(true);
                        phoneVO.setPinModifiedOn(currentDate);
                        if(!"SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                        	phoneVO.setSmsPin(BTSLUtil.encryptText(msisdn.getPin())); 
                        }else {
                        	phoneVO.setSmsPin(msisdn.getPin());
                        }
                        phoneVO.setIdGenerate(true);
                        phoneVO.setPinRequired(PretupsI.YES);
                        phoneVO.setDescription(msisdn.getDescription());
                        if(msisdn.getStkProfile()==null)
                        {
                        	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_FOUND);
                        }
                        else if(!(msisdn.getStkProfile().equals(stkProfile)))
                        {
                        	throw new BTSLBaseException("ModifyChannelUser", methodName, PretupsErrorCodesI.STK_PROFILE_NOT_VALID);
                        }
                        phoneVO.setPhoneProfile(msisdn.getStkProfile());
                    }
                    if (!BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                        /*
                         * modified by ashishT
                         * to set default **** as the pin on jsp.
                         */
                        if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                            phoneVO.setShowSmsPin("****");
                            phoneVO.setConfirmSmsPin("****");
                        }
                        // set the default value *****
                        else {
                            phoneVO.setShowSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
                            phoneVO.setConfirmSmsPin(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin())));
                        }
                    }
                    phoneVO.setUserId(channelUserVO.getUserID());
                    // set the default values
                    phoneVO.setCreatedBy(senderVO.getUserID());
                    phoneVO.setModifiedBy(senderVO.getUserID());
                    phoneVO.setCreatedOn(currentDate);
                    phoneVO.setModifiedOn(currentDate);
                    ArrayList languageList=LocaleMasterDAO.loadLocaleMasterData();
                    boolean flag1=true;
                    if (!BTSLUtil.isNullString(requestVO.getData().getLanguage())) {
                    for(int k=0;k<languageList.size();k++)
                    {
                    	if(((ListValueVO)languageList.get(k)).getValue().equalsIgnoreCase(requestVO.getData().getLanguage()))
                    	{
                    		flag1=false;
                    	}
                    }
                    if(flag1==true)
                    {
                    	throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.LANGUAGE_DOES_NOT_EXIST);
                    }
                    }
                    if (!BTSLUtil.isNullString(requestVO.getData().getLanguage())) {
                        final String lang_country[] = (requestVO.getData().getLanguage()).split("_");
                        phoneVO.setPhoneLanguage(lang_country[0]);
                        phoneVO.setCountry(lang_country[1]);
                    } else {
                        phoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                        phoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                    }
                    if(!(BTSLUtil.isValidMSISDN(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                    }
                    if(!(BTSLUtil.isValidMSISDNDigit(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXT_USRADD_INVALID_MSISDN);
                    }
                    if(!(BTSLUtil.isValidMSISDNLength(phoneVO.getMsisdn())))
                    {
                    	throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.EXTSYS_MSISDN_MAP_LENGTH_EXCEEDS);
                    }
                    phoneVO.setPinModifiedOn(currentDate);
                    final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL
                        .getMSISDNPrefix(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn())));
                    phoneVO.setPrefixID(prefixVO.getPrefixID());
                    if (msisdn.getIsprimary().equals("Y")) {
                        channelUserVO.setMsisdn(msisdn.getPhoneNo());
                        phoneVO.setPrimaryNumber(PretupsI.YES);
                    } else {
                        phoneVO.setPrimaryNumber(PretupsI.NO);
                    }
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                        networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn.getPhoneNo()));
                        boolean numberAllowed = false;
                        if (networkPrefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_IN);
                            if (!numberAllowed) {
                                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_IN);
                            }
                        } else {
                            numberAllowed = new NumberPortDAO().isExists(con, msisdn.getPhoneNo(), "", PretupsI.PORTED_OUT);
                            if (numberAllowed) {
                                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_PORTED_OUT);
                            }
                        }
                    }

                    if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.ERROR_ERP_CHNL_USER_MSISDN_ALREADY_EXIST, arr);
                    }
                    if ((!((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW))).booleanValue()) && phoneVO.isPinModifyFlag()) {

                        if (userDAO.checkPasswordHistory(con, PretupsI.USER_PIN_MANAGEMENT, phoneVO.getUserId(), PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()),
                                        BTSLUtil.encryptText(phoneVO.getShowSmsPin()))) {
                            LOG.error(methodName, "Error: Pin exist in password_history table");
                            throw new BTSLBaseException(this, methodName, "channeluser.changepin.error.pinhistory", 0, new String[] { String
                                            .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()), phoneVO.getMsisdn() }, "Detail");
                        }
                    }
                    phoneList.add(phoneVO);
                } else {
                    if (oldPhoneList != null && !oldPhoneList.isEmpty()) {
                        if (i < oldPhoneList.size()) {
                            oldPhoneVO = (UserPhoneVO) oldPhoneList.get(oldPhoneList.size() - (i + 1));
                            phoneVO = new UserPhoneVO();
                            phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
                        }
                    }
                    if (!BTSLUtil.isNullString(phoneVO.getUserPhonesId())) {
                        if (!phoneVO.isIdGenerate()) {
                            phoneVO.setOperationType("D");
                        }
                        phoneList.add(phoneVO);

                    }
                }
                oldPhoneVO = null;
                phoneVO = null;
            }
            
            for(int k=0;k<msisdnDelete.length;k++) {
            	if(!msisdnDelete[k]) {
            	 oldPhoneVO = (UserPhoneVO) oldPhoneList.get(k);
            	 phoneVO = new UserPhoneVO();
                 phoneVO.setUserPhonesId(oldPhoneVO.getUserPhonesId());
                 if (!BTSLUtil.isNullString(phoneVO.getUserPhonesId())) {
                	 if (!phoneVO.isIdGenerate()) {
                         phoneVO.setOperationType("D");
                     }
                     phoneList.add(phoneVO);

                 }
            	}
                 oldPhoneVO = null;
                 phoneVO = null;
            }

        }
        return phoneList;
    }
}

package com.restapi.user.service;

import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserForm;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;

@io.swagger.v3.oas.annotations.tags.Tag(name = "${DeleteStaffUserController.name}", description = "${DeleteStaffUserController.desc}")//@Api(tags= "Staff Users", value="Staff Users")
@RestController
@RequestMapping(value = "/v1/staffUser")
public class DeleteStaffUserController {
	public static final Log log = LogFactory.getLog(DeleteStaffUserController.class.getName());
	
	@GetMapping(value ="/delete" , produces = MediaType.APPLICATION_JSON)
	@ResponseBody
	/*@ApiOperation(tags= "Staff Users", value = "Delete a Staff User", response = UserDeleteResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = UserDeleteResponseVO.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

	@io.swagger.v3.oas.annotations.Operation(summary = "${testendpoint.summary}", description="${testendpoint.description}",

			responses = {
					@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = com.btsl.util.Constants.API_SUCCESS_RESPONSE_CODE, description = com.btsl.util.Constants.API_SUCCESS_RESPONSE_DESC, content = {
							@io.swagger.v3.oas.annotations.media.Content(
									mediaType = "application/json",
									array = @io.swagger.v3.oas.annotations.media.ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserDeleteResponseVO.class))
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

	public UserDeleteResponseVO deleteUser(
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 @Parameter(description = "userID", required = true)
			 @RequestParam("idValue") String id,
			 HttpServletResponse response1 
			) throws Exception {
		final String methodName =  "deleteUser";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
       MComConnectionI mcomCon = null;
       UserDeleteResponseVO response = null;
       ChannelUserVO sessionUserVO = new ChannelUserVO();
       ChannelUserVO loggedinsessionUserVO = new ChannelUserVO();
       response = new UserDeleteResponseVO();
       try {
       	/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
       	OAuthUser oAuthUserData=new OAuthUser();
   		//UserDAO userDao = new UserDAO();
   		
   		oAuthUserData.setData(new OAuthUserData());
   		response.setService("DELETESTAFFUSER");
   		OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
   		String loginId =  oAuthUserData.getData().getLoginid();
   		ChannelUserVO staffUserVO = new ChannelUserVO();
			mcomCon = new MComConnection();
           con=mcomCon.getConnection();
			
			String identifiervalue = loginId ;
			
			final UserDAO userDAO = new UserDAO();
			 final UserForm theForm = new UserForm();
			 loggedinsessionUserVO  = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con,identifiervalue);
			 
			 UserPhoneVO phoneVO = null;
	            UserPhoneVO oldPhoneVO = null;
	            
				if (loggedinsessionUserVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT)
						|| loggedinsessionUserVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY)) {
					staffUserVO = new UserDAO().loadUserDetailsFormUserID(con, id);
					sessionUserVO = (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con, staffUserVO.getLoginID()); // id of the user going to be deleted
					phoneVO = new ChannelUserDAO().loadUserPhoneDetails(con, sessionUserVO.getParentID());
				} else {
					sessionUserVO= (ChannelUserVO) userDAO.loadAllUserDetailsByLoginID(con,identifiervalue);
					phoneVO = new ChannelUserDAO().loadUserPhoneDetails(con, loggedinsessionUserVO.getUserID());
				}
             
			 
             // oldPhoneVO=new UserPhoneVO(phoneVO);
             if (phoneVO != null) {
                 oldPhoneVO = new UserPhoneVO(phoneVO);
                 if (PretupsI.NOT_AVAILABLE.equals(phoneVO.getMsisdn())) {
                     phoneVO.setMsisdn("");
                 }
                 phoneVO.setConfirmSmsPin(phoneVO.getShowSmsPin());
                 final ArrayList msisdnList = new ArrayList();
                 final ArrayList oldMsisdnList = new ArrayList();
                 msisdnList.add(phoneVO);
                 oldMsisdnList.add(oldPhoneVO);
                 theForm.setMsisdnList(msisdnList);
                 theForm.setOldMsisdnList(oldMsisdnList);
             }
             final Date currentDate = new Date();
             Locale locale = null;
             ArrayList userList=null;
         	if (loggedinsessionUserVO.getDomainID().equals(PretupsI.OPERATOR_TYPE_OPT)
					|| loggedinsessionUserVO.getCategoryCode().equals(PretupsI.OPERATOR_CATEGORY)) {
		
         		sessionUserVO.setActiveUserID(sessionUserVO.getParentID());
         		userList= new UserWebDAO().loadChildUserList(con, sessionUserVO.getParentID(), PretupsI.STAFF_USER_TYPE, "%%%");
         	}else {
         		sessionUserVO.setActiveUserID(sessionUserVO.getUserID());	
        		userList= new UserWebDAO().loadChildUserList(con, sessionUserVO.getUserID(), PretupsI.STAFF_USER_TYPE, "%%%");
         	}
             
             final int length = userList.size();
             for (int i = 0; i < length; i++) {
                 if (((ListValueVO) userList.get(i)).getValue().equals(sessionUserVO.getActiveUserID())) {
                     userList.remove(i);
                     break;
                 }
             }
             boolean flag = false;
             for (int i = 0; i < length; i++) {
                 if (((ListValueVO) userList.get(i)).getValue().equals(id)) {
                	 flag=true;
                     break;
                 }
             }
             if(!flag)
            	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0,null,null); 
             staffUserVO = new UserDAO().loadUserDetailsFormUserID(con, id);
             staffUserVO.setNetworkID(sessionUserVO.getNetworkID());
             staffUserVO.setModifiedOn(currentDate);
             staffUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
             staffUserVO.setStatus(PretupsI.USER_STATUS_DELETED);
             staffUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);
             this.constructFormToVO(theForm, staffUserVO, sessionUserVO);
             final ArrayList list = new ArrayList();
             list.add(staffUserVO);
             final int deleteCount = userDAO.deleteSuspendUser(con, list);
             if (deleteCount <= 0) {
                 con.rollback();
                 log.error(methodName, "Error: while Deleting User");
                 throw new BTSLBaseException(this, methodName, "error.general.processing");
             }
             if (theForm.getMsisdnList() != null) {
                 final UserPhoneVO phoneVO2 = (UserPhoneVO) theForm.getMsisdnList().get(0);
                 if (phoneVO2 != null && !BTSLUtil.isNullString(phoneVO2.getMsisdn()) && !PretupsI.NOT_APPLICABLE.equals(phoneVO2.getMsisdn())) {
                     staffUserVO.setMsisdn(phoneVO2.getMsisdn());
                     locale = new Locale(phoneVO2.getPhoneLanguage(), phoneVO2.getCountry());
                 }
             }
             con.commit();
             BTSLMessages btslMessage = null;
             PushMessage pushMessage = null;
             String parentId = null;
             final String arr[] = { staffUserVO.getUserName() };

             if (PretupsI.USER_STATUS_DELETED.equals(staffUserVO.getStatus())) {
                 if (locale == null) {
                     
                     locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
                 }
         
                 if (!BTSLUtil.isNullString(staffUserVO.getMsisdn()) && !"".equals(staffUserVO.getMsisdn()) && !PretupsI.NOT_AVAILABLE.equals(staffUserVO
                                 .getMsisdn())) {
                     btslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
                    pushMessage = new PushMessage(staffUserVO.getMsisdn(), btslMessage, "", "", locale, staffUserVO.getNetworkID());
                     pushMessage.push();
                     // Email for pin & password
                     if (SystemPreferences.IS_EMAIL_SERVICE_ALLOW && !BTSLUtil.isNullString(staffUserVO.getEmail())) {
                         final String subject = BTSLUtil.getMessage(locale,
                                         "user.staffuser.savestaffuserdetails.success.delete", arr);
                         final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage, locale, staffUserVO.getNetworkID(),
                                         "Email will be delivered shortly", staffUserVO, sessionUserVO);
                         emailSendToUser.sendMail();
                     }
                 } else {
                     if (BTSLUtil.isNullString(parentId)) {
                         parentId = sessionUserVO.getUserID();
                     }
                     final UserPhoneVO phoneVO1 = new ChannelUserDAO().loadUserPhoneDetails(con, parentId);
                     locale = new Locale(phoneVO1.getPhoneLanguage(), phoneVO1.getCountry());
                     btslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER_STAFF, new String[] { staffUserVO.getUserName() });
           
                     pushMessage = new PushMessage(phoneVO1.getMsisdn(), btslMessage, "", "", locale, staffUserVO.getNetworkID());
                     pushMessage.push();
                     // Email for pin & password
                     if (SystemPreferences.IS_EMAIL_SERVICE_ALLOW && !BTSLUtil.isNullString(sessionUserVO.getEmail())) {
                         final String subject = BTSLUtil.getMessage(locale,
                                         "user.staffuser.savestaffuserdetails.success.delete", arr);
                         final ChannelUserVO tmpStaffUserVO = new ChannelUserVO();
                         BeanUtils.copyProperties(tmpStaffUserVO, staffUserVO);
                         tmpStaffUserVO.setEmail(sessionUserVO.getEmail());
                         final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, btslMessage, locale, staffUserVO.getNetworkID(),
                                         "Email will be delivered shortly", tmpStaffUserVO, sessionUserVO);
                         emailSendToUser.sendMail();
                     }
                 }
             }
             btslMessage = new BTSLMessages("user.staffuser.savestaffuserdetails.success.delete", arr, "loadFirst");
             ChannelUserLog.log("DELETESTAFFUSR", staffUserVO, sessionUserVO, true, null);
             response.setMessage(staffUserVO.getUserName()+"Staff User Successfully deleted");
             response.setStatus("200");
	}catch (BTSLBaseException be) {
		 
		 log.error(methodName, "Exception:e=" + be);
	     log.errorTrace(methodName, be);
		   String resmsg  = RestAPIStringParser.getMessage(new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE),(String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)), be.getMessage(), be.getArgs());
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
       finally {
           try {
           	if (mcomCon != null) {
   				mcomCon.close("DeleteStaffUserController#deleteUser");
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
	private void constructFormToVO(UserForm theForm, ChannelUserVO p_channelUserVO, ChannelUserVO sessionUserVO) throws ParseException {
        final String methodName = "constructFormToVO";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  p_channelUserVO=" + p_channelUserVO);
        }
        String password = null;
		//p_channelUserVO.setUserID(theForm.getUserId());
		p_channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
		//p_channelUserVO.setUserName(theForm.getChannelUserName());
		//p_channelUserVO.setLoginID(theForm.getWebLoginID());
		// while inserting encrypt the password
		/*
		 * if (!BTSLUtil.isNullString(theForm.getShowPassword())) { password =
		 * BTSLUtil.encryptText(theForm.getShowPassword()); }
		 */
		p_channelUserVO.setPassword(password);
		p_channelUserVO.setPasswordModifyFlag(true);
		//p_channelUserVO.setCategoryCode(theForm.getCategoryCode());
		p_channelUserVO.setParentID(sessionUserVO.getUserID());
		
		p_channelUserVO.setOwnerID(sessionUserVO.getOwnerID());
		//p_channelUserVO.setAllowedIps(theForm.getAllowedIPs());
		final StringBuffer str = new StringBuffer();
		/*
		 * theForm.getAllowedDays returns an string array but in DB we
		 * insert a single
		 * string value of the allowed days like 1,4,7, for this prupose
		 * convert the string
		 * array into string
		 */
		/*
		 * if (theForm.getAllowedDays() != null && theForm.getAllowedDays().length > 0)
		 * { str.append(theForm.getAllowedDays()[0]); for (int i = 1, j =
		 * theForm.getAllowedDays().length; i < j; i++) { str.append("," +
		 * theForm.getAllowedDays()[i]); } }
		 */

		p_channelUserVO.setAllowedDays(str.toString());
         // p_channelUserVO.setFromTime(theForm.getAllowedFormTime());
         // p_channelUserVO.setToTime(theForm.getAllowedToTime());
         // p_channelUserVO.setEmpCode(theForm.getEmpCode());
         // p_channelUserVO.setContactNo(theForm.getContactNo());
         // p_channelUserVO.setEmail(theForm.getEmail());
         // p_channelUserVO.setDesignation(theForm.getDesignation());
		// while adding Staff user userType value will be STAFF
		p_channelUserVO.setUserType(PretupsI.STAFF_USER_TYPE);
      
		p_channelUserVO.setContactPerson(sessionUserVO.getContactPerson());
		p_channelUserVO.setUserGrade(sessionUserVO.getUserGrade());
		p_channelUserVO.setTransferProfileID(sessionUserVO.getTransferProfileID());
		p_channelUserVO.setCommissionProfileSetID(sessionUserVO.getCommissionProfileSetID());
		p_channelUserVO.setInSuspend(sessionUserVO.getInSuspend());
		p_channelUserVO.setOutSuspened(sessionUserVO.getOutSuspened());
		p_channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
         // p_channelUserVO.setAddress1(theForm.getAddress1());
		//p_channelUserVO.setAddress2(theForm.getAddress2());
		//p_channelUserVO.setCity(theForm.getCity());
		//p_channelUserVO.setState(theForm.getState());
		//p_channelUserVO.setCountry(theForm.getCountry());
		//p_channelUserVO.setSsn(theForm.getSsn());
		// Added for RSA Authentication
		p_channelUserVO.setRsaFlag(theForm.getRsaAuthentication());
         // p_channelUserVO.setUserNamePrefix(theForm.getUserNamePrefixCode());
		//p_channelUserVO.setExternalCode(theForm.getExternalCode());
		//p_channelUserVO.setShortName(theForm.getShortName());
		/*
		 * if (!BTSLUtil.isNullString(theForm.getAppointmentDate())) {
		 * p_channelUserVO.setAppointmentDate(BTSLUtil.getDateFromDateString(theForm.
		 * getAppointmentDate())); }
		 */
		p_channelUserVO.setOutletCode(sessionUserVO.getOutletCode());
		p_channelUserVO.setSubOutletCode(sessionUserVO.getSubOutletCode());
		// modified for staff user approval
		if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(sessionUserVO.getDomainID())) {
       
		    final ChannelUserVO parentVO = theForm.getStaffParentVO();
		    p_channelUserVO.setNetworkID(parentVO.getNetworkID());
		    p_channelUserVO.setParentID(parentVO.getUserID());
         
		    p_channelUserVO.setOwnerID(parentVO.getOwnerID());
		    p_channelUserVO.setContactPerson(parentVO.getContactPerson());
		    p_channelUserVO.setUserGrade(parentVO.getUserGrade());
		    p_channelUserVO.setTransferProfileID(parentVO.getTransferProfileID());
		    p_channelUserVO.setCommissionProfileSetID(parentVO.getCommissionProfileSetID());
		    p_channelUserVO.setInSuspend(parentVO.getInSuspend());
		    p_channelUserVO.setOutSuspened(parentVO.getOutSuspened());
		    p_channelUserVO.setModifiedBy(parentVO.getUserID());
		    p_channelUserVO.setOutletCode(parentVO.getOutletCode());
		    p_channelUserVO.setSubOutletCode(parentVO.getSubOutletCode());
		} else {
		    p_channelUserVO.setNetworkID(sessionUserVO.getNetworkID());
		    p_channelUserVO.setParentID(sessionUserVO.getUserID());
       
		    p_channelUserVO.setOwnerID(sessionUserVO.getOwnerID());
		    p_channelUserVO.setContactPerson(sessionUserVO.getContactPerson());
		    p_channelUserVO.setUserGrade(sessionUserVO.getUserGrade());
		    p_channelUserVO.setTransferProfileID(sessionUserVO.getTransferProfileID());
		    p_channelUserVO.setCommissionProfileSetID(sessionUserVO.getCommissionProfileSetID());
		    p_channelUserVO.setInSuspend(sessionUserVO.getInSuspend());
		    p_channelUserVO.setOutSuspened(sessionUserVO.getOutSuspened());
		    p_channelUserVO.setModifiedBy(sessionUserVO.getActiveUserID());
		    p_channelUserVO.setOutletCode(sessionUserVO.getOutletCode());
		    p_channelUserVO.setSubOutletCode(sessionUserVO.getSubOutletCode());
		}
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting  p_channelUserVO=" + p_channelUserVO);
        }
    }


}

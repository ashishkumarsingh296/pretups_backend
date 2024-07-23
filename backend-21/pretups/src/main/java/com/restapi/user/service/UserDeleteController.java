package com.restapi.user.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


import jakarta.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserDeletionBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.master.businesslogic.LookupsDAO;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OAuthenticationUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.web.user.businesslogic.UserWebDAO;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameter;


/*@Path("/v1/channelUsers")*/
@io.swagger.v3.oas.annotations.tags.Tag(name = "${UserDeleteController.name}", description = "${UserDeleteController.desc}")//@Api(tags= "Channel Users", value="Channel Users")
@RestController
@RequestMapping(value = "/v1/channelUsers")
public class UserDeleteController {
	public static final Log log = LogFactory.getLog(UserDeleteController.class.getName());
	/*@DELETE
    @Path("/{id}")*/
	
	@PostMapping(value ="/delete/idValue" , produces = MediaType.APPLICATION_JSON)
	@ResponseBody
   /* @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
	/*@ApiOperation(tags= "Channel Users", value = "Delete a Channel User", response = UserDeleteResponseVO.class,
			authorizations = {
    	            @Authorization(value = "Authorization")})
	
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "OK", response = UserDeleteResponseVO.class),
	        @ApiResponse(code = 400, message = "Bad Request"),
	        @ApiResponse(code = 401, message = "Unauthorized"),
	        @ApiResponse(code = 404, message = "Not Found")
	        })
	*/

    @io.swagger.v3.oas.annotations.Operation(summary = "${delete.summary}", description="${delete.description}",

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

    public UserDeleteResponseVO deleteUser(
			 @Parameter(hidden = true) @RequestHeader MultiValueMap<String, String> headers,
			 @Parameter(description = SwaggerAPIDescriptionI.USR_SELECT_DEL, required = true)//allowableValues = "LOGINID,MSISDN")
			 @RequestParam("idType") String idtype,
			 @Parameter(required = true)
			 @RequestParam("remarks") String remarks,
			 @Parameter(required = true)
			 @RequestParam("extnwcode") String extnwcode,
			 @Parameter(description = SwaggerAPIDescriptionI.SELECTED_VALUE, required = true)
			 @RequestParam("idValue") String id,
			 HttpServletResponse response1 
			) throws IOException, SQLException, BTSLBaseException {
		final String methodName =  "deleteUser";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered ");
		}
		Connection con = null;
        MComConnectionI mcomCon = null;
        UserDeleteResponseVO response = null;
        LookupsDAO lookupsDAO = null;
        UserVO sessionUserVO = new UserVO();
        response = new UserDeleteResponseVO();
        String messageArray[] = new String[1];
        try {
        	/*
			 * Authentication
			 * @throws BTSLBaseException
			 */
        	OAuthUser oAuthUserData=new OAuthUser();
    		//UserDAO userDao = new UserDAO();
    		
    		oAuthUserData.setData(new OAuthUserData());
    		response.setService("DELETECHANNELUSER");
    		OAuthenticationUtil.validateTokenApi(oAuthUserData,headers,response1);
    		//response.setService("");
    		
    		String loginId =  oAuthUserData.getData().getLoginid();
    		String msisdn =  oAuthUserData.getData().getMsisdn();
    		
			
			mcomCon = new MComConnection();
			lookupsDAO = new LookupsDAO();
            con=mcomCon.getConnection();
			String networkCode = extnwcode;
			if (!BTSLUtil.isNullString(extnwcode)) {
                NetworkVO networkVO = (NetworkVO) NetworkCache.getNetworkByExtNetworkCode(extnwcode);
                if(networkVO==null){
                 messageArray[0]= extnwcode;
               throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_INVALID, messageArray);
                }
            }
			else
			{
				 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_EXTNWCODE_BLANK, 0,null,null);
			}
            if(BTSLUtil.isNullString(remarks)){
            	 throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.REMARKS_REQUIRED, 0,null,null);
            }
			boolean validateuser = false;
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			String identifiertype= idtype;
			String identifiervalue = id;
		
			validateuser = pretupsRestUtil.validateUserForActOrPreAct(identifiertype, identifiervalue, networkCode, con);
			if(validateuser == false){
				throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVALID_USER, 0,null,null);
			}
			
			final UserDAO userDAO = new UserDAO();
			final UserWebDAO userwebDAO = new UserWebDAO();
			UserVO userVO = new UserVO();
			
			 if(PretupsI.MSISDN.equalsIgnoreCase(identifiertype)){
				 userVO = (UserVO) userDAO.loadUserDetailsByMsisdn(con,id);
	            	if(userVO==null){
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
					if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(userVO.getStatus())||PretupsI.USER_STATUS_DELETED.equalsIgnoreCase(userVO.getStatus()) ){
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
	            }
	            else if(PretupsI.LOGINID.equalsIgnoreCase(identifiertype)){
	            	userVO = (UserVO) userDAO.loadUserDetailsByLoginId(con,id);
	            	if(userVO==null){
	            		throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
	            	if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(userVO.getStatus())||PretupsI.USER_STATUS_DELETED.equalsIgnoreCase(userVO.getStatus()) ){
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
					}
	            }
			 
		sessionUserVO = (UserVO) userDAO.loadUsersDetails(con,msisdn); // logged in useid.
			 
			 ArrayList lookupList = new ArrayList();
			 
         if(PretupsI.MSISDN.equalsIgnoreCase(idtype)){
         	userVO = (UserVO) userDAO.loadUserDetailsByMsisdn(con,id);
         	if(userVO==null){
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_NOT_FOUND_DELETE, 0,null,null);
				}
				if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(userVO.getStatus()) 
	         			||PretupsI.USER_STATUS_DELETE_REQUEST.equalsIgnoreCase(userVO.getStatus()) 
	         			||PretupsI.USER_STATUS_NEW.equalsIgnoreCase(userVO.getStatus())
	         			||PretupsI.USER_STATUS_SUSPEND_REQUEST.equalsIgnoreCase(userVO.getStatus())
	         			||PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST.equalsIgnoreCase(userVO.getStatus())
	         			||PretupsI.USER_STATUS_BARRED.equalsIgnoreCase(userVO.getStatus())
	         			||PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE.equalsIgnoreCase(userVO.getStatus())
	         			){
					lookupList=lookupsDAO.loadLookupsFromLookupCode(con, userVO.getStatus(),PretupsI.USER_STATUS_TYPE);
					   

					LookupsVO lookupsVO = (LookupsVO) lookupList.get(0); 
					messageArray[0]=lookupsVO.getLookupName();
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_NOT_DELETED,new String[] {lookupsVO.getLookupName()});		
					}
         }
         else if(PretupsI.LOGINID.equalsIgnoreCase(idtype)){
         	userVO = (UserVO) userDAO.loadAllUserDetailsByLoginID(con,id);
         	if(userVO==null){
         		throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_NOT_FOUND_DELETE, 0,null,null);
				}
         	
      

         	
         	if(PretupsI.USER_STATUS_CANCELED.equalsIgnoreCase(userVO.getStatus()) 
         			||PretupsI.USER_STATUS_DELETE_REQUEST.equalsIgnoreCase(userVO.getStatus()) 
         			||PretupsI.USER_STATUS_NEW.equalsIgnoreCase(userVO.getStatus())
         			||PretupsI.USER_STATUS_SUSPEND_REQUEST.equalsIgnoreCase(userVO.getStatus())
         			||PretupsI.USER_STATUS_BAR_FOR_DEL_REQUEST.equalsIgnoreCase(userVO.getStatus())
         			||PretupsI.USER_STATUS_BARRED.equalsIgnoreCase(userVO.getStatus())
         			||PretupsI.USER_STATUS_BAR_FOR_DEL_APPROVE.equalsIgnoreCase(userVO.getStatus())
         			){
        
         		
         		lookupList=lookupsDAO.loadLookupsFromLookupCode(con, userVO.getStatus(),PretupsI.USER_STATUS_TYPE);
         		LookupsVO lookupsVO = (LookupsVO) lookupList.get(0);
         		messageArray[0]=lookupsVO.getLookupName();
				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.USER_NOT_DELETED,new String[] {lookupsVO.getLookupName()});
			}
         }
			
        // IF channelAdmin LOGS IN IGNORE BELOW VALIDATION CHECK.
         if (sessionUserVO.getDomainID()!=null  && !sessionUserVO.getDomainID().toUpperCase().equals(PretupsI.DOMAIN_TYPE_OPT)) { //by subesh
       	C2STransferDAO c2STransferDAO = new C2STransferDAO();
			boolean flag = false;
			ArrayList allowedList = c2STransferDAO.loadC2SRulesListForChannelUserAssociation(con,networkCode);
			 ChannelTransferRuleVO channelTransferRuleVO = null;
			for(int i=0;i<allowedList.size();i++){
				channelTransferRuleVO = new ChannelTransferRuleVO();
				channelTransferRuleVO = (ChannelTransferRuleVO) allowedList.get(i);
				if(channelTransferRuleVO.getFromCategory().equalsIgnoreCase(sessionUserVO.getCategoryCode())&& channelTransferRuleVO.getToCategory().equalsIgnoreCase(userVO.getCategoryCode())){
					flag = true;
				}
			}
			if(flag==false){
				throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
			}
			if(!userVO.getOwnerID().equalsIgnoreCase(sessionUserVO.getUserID()) && !userVO.getParentID().equalsIgnoreCase(sessionUserVO.getUserID())){
				 throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVALID_USER, 0,null,null);
			}
         }
            /*
             * Before deleting three checks will be perform
             * a)Check whether the child user is active or not
             * b)Check the balance of the deleted user
             * c)Check for no O2C Transfer pending (closed and canceled
             * Txn)
             */
			
            boolean isBalanceFlag = false;
            boolean isO2CPendingFlag = false;
            boolean isSOSPendingFlag = false;
            boolean isLRPendingFlag = false;
            final Date currentDate = new Date();
            ArrayList<UserEventRemarksVO> deleteSuspendRemarkList = null;
            UserEventRemarksVO userRemarksVO = null;
            final boolean isChildFlag = userDAO.isChildUserActive(con, userVO.getUserID());

            if (isChildFlag) {
                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DEL_CHILD_USR_EXIST, 0,null,null);
            }

            else {
            	// Checking SOS Pending transactions
            	if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
			        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
			        isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, userVO.getUserID());
				}
            }
            if(isSOSPendingFlag){
                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DEL_SOS_PENDING, 0,null,null);
            }else {
            	// Checking for pending LR transactions
        		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
        			UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
        			UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
        			userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(userVO.getUserID(), con, false, null);
        			if (userTrfCntVO!=null) 
        				isLRPendingFlag = true;
        		}
            }
            if(isLRPendingFlag){
                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DEL_LR_PENDING, 0,null,null);
            }else{ 
            	// Checking O2C Pending transactions
                final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
                isO2CPendingFlag = transferDAO.isPendingTransactionExist(con, userVO.getUserID());
            }
            int deleteCount = 0;
            boolean isRestrictedMsisdnFlag = false;
            boolean isbatchFocPendingTxn = false;
            if (isO2CPendingFlag) {
                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DEL_O2C_PENDING, 0,null,null);
            } else {
                // Checking Batch FOC Pending transactions Ved -
                // 07/08/06
                final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
                isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, userVO.getUserID());
            }
            if (isbatchFocPendingTxn) {
                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DEL_FOC_PENDING, 0,null,null);
            } else {
                if (PretupsI.STATUS_ACTIVE.equals(userVO.getCategoryVO().getRestrictedMsisdns())) {
                    final RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
                    isRestrictedMsisdnFlag = restrictedSubscriberDAO.isSubscriberExistByChannelUser(con, userVO.getUserID());
                }
            }
            if (isRestrictedMsisdnFlag) {
                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.DEL_RESTRICTED_MSISDN, 0,null,null);
            }
            userVO.setUserID(userVO.getUserID());
            userVO.setPreviousStatus(userVO.getStatus());
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_DELETION_APPROVAL)).booleanValue()) {
                userVO.setStatus(PretupsI.USER_STATUS_DELETE_REQUEST);
            } else {
                isBalanceFlag = userDAO.isUserBalanceExist(con, userVO.getUserID());
                userVO.setStatus(PretupsI.USER_STATUS_DELETED);
                if (isBalanceFlag) {
                    // 6.5
                    final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
                    ArrayList<UserBalancesVO> userBal = null;
                    UserBalancesVO userBalancesVO = null;
                    final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                    final ChannelUserVO fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, userVO.getUserID(), false, currentDate,false);
                    fromChannelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_WEB);
                    final ChannelUserVO toChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, fromChannelUserVO.getOwnerID(), false, currentDate,false);
                    userBal = userBalancesDAO.loadUserBalanceForDelete(con, fromChannelUserVO.getUserID());// user
                    // to
                    // be
                    // deleted
                    Iterator<UserBalancesVO> itr = userBal.iterator();
                    itr = userBal.iterator();
                    boolean sendMsgToOwner = false;
                    long totBalance = 0;
                    while (itr.hasNext()) {
                        userBalancesVO = itr.next();
                        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() || fromChannelUserVO
                                        .getOwnerID().equals(userVO.getUserID())) {
                            UserDeletionBL.updateBalNChnlTransfersNItemsO2C(con, fromChannelUserVO, toChannelUserVO, PretupsI.REQUEST_SOURCE_TYPE_WEB,
                                            PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
                        } else {

                        	if(!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(toChannelUserVO.getStatus()))
                        	{
                            UserDeletionBL.updateBalNChnlTransfersNItemsC2C(con, fromChannelUserVO, toChannelUserVO, sessionUserVO.getUserID(),
                                            PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
                            sendMsgToOwner = true; 
                            totBalance += userBalancesVO.getBalance();
                        	}
                        	else
                        		throw new BTSLBaseException(this, "save",PretupsErrorCodesI.DEL_OWNER_SUSPENDED, 0,null,null);
                        }
                    }
                    //ASHU
                    if(sendMsgToOwner) {
                    	ChannelUserVO prntChnlUserVO = new ChannelUserDAO().loadChannelUserByUserID(con, fromChannelUserVO.getOwnerID());
                        String msgArr [] = {fromChannelUserVO.getMsisdn(),PretupsBL.getDisplayAmount(totBalance)};
                        final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
                        final PushMessage pushMessageToOwner = new PushMessage(prntChnlUserVO.getMsisdn(), sendBtslMessageToOwner, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                                        (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), fromChannelUserVO.getNetworkID());
                        pushMessageToOwner.push();   
                    } 
                }
            }

            userVO.setLastModified(userVO.getLastModified());
            if(null==sessionUserVO.getActiveUserID()) {
            	userVO.setModifiedBy(sessionUserVO.getUserID());	
            }else {
            userVO.setModifiedBy(sessionUserVO.getActiveUserID());
            }
            userVO.setModifiedOn(currentDate);            /*
             * set the old status value into the previous status
             */
           
            final ArrayList list = new ArrayList();
            list.add(userVO);
            deleteCount = userDAO.deleteSuspendUser(con, list);
            if (deleteCount <= 0) {
                con.rollback();
                log.error(methodName, "Error: while Deleting User");
                throw new BTSLBaseException(this, "save",PretupsErrorCodesI.GENERAL_PROCESSING_ERROR_EXCEPTION, 0,null,null);
            }
            /*
             * Added By Babu Kunwar for inserting Remarks For Deleting
             * any Channel User in USER_EVENT_REMARKS table.
             * Dated:15/02/2011( Wednesday)
             */
            // if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))).booleanValue())
            // {
            if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue()){
                
                if (deleteCount > 0) {
                    int deleteRemarkCount = 0;
                    deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
                    userRemarksVO = new UserEventRemarksVO();
                    userRemarksVO.setCreatedBy(sessionUserVO.getCreatedBy());
                    userRemarksVO.setCreatedOn(currentDate);
                    
                    userRemarksVO.setEventType(PretupsI.DELETE_REQUEST_EVENT);
                    userRemarksVO.setMsisdn(userVO.getMsisdn());
                    userRemarksVO.setRemarks(remarks);
                    userRemarksVO.setUserID(userVO.getUserID());
                    userRemarksVO.setUserType(userVO.getUserType());
                    userRemarksVO.setModule(PretupsI.C2S_MODULE);
                    deleteSuspendRemarkList.add(userRemarksVO);
                    deleteRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
                    if (deleteRemarkCount <= 0) {
                        con.rollback();
                        log.error(methodName, "Error: while inserting into userEventRemarks Table");
                        throw new BTSLBaseException(this, "save",PretupsErrorCodesI.GENERAL_PROCESSING_ERROR_EXCEPTION, 0,null,null);
                    }
                }
            }

            // }
            con.commit();
            final String arr[] = { userVO.getUserName() };
            BTSLMessages btslMessage = null;
            if (PretupsI.USER_STATUS_DELETED.equals(userVO.getStatus())) {
                btslMessage = new BTSLMessages(PretupsErrorCodesI.DEL_SUCCESS, arr, "DeleteSuccess");

                final BTSLMessages sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
                final PushMessage pushMessage = new PushMessage(userVO.getMsisdn(), sendBtslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                                (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), sessionUserVO.getNetworkID());
                pushMessage.push();
                // Email for pin & password
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(userVO.getEmail())) {
                	final String subject =  RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), PretupsErrorCodesI.SUB_MAIL_DELETE_USER, null);
                    final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, sendBtslMessage,new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), userVO.getNetworkID(),
                                    "Email has ben delivered recently", userVO, sessionUserVO);
                    emailSendToUser.sendMail();
                }
            } else {
                btslMessage = new BTSLMessages(PretupsErrorCodesI.DEL_SUCC_APPROVAL_REQD, arr, "DeleteSuccess");
            }

            userVO.setLoginID(sessionUserVO.getLoginID());
            userVO.setUserName(userVO.getUserName());
            userVO.setMsisdn(userVO.getMsisdn());

            if (userVO.getStatus().equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
                ChannelUserLog.log("DELREQCHNLUSR", userVO, sessionUserVO, false, null);
            } else if (userVO.getStatus().equals(PretupsI.USER_STATUS_DELETED)) {
                ChannelUserLog.log("DELCHNLUSR", userVO, sessionUserVO, false, null);
            }
     	   response.setStatus(PretupsI.RESPONSE_SUCCESS.toString());
           response.setMessageCode(btslMessage.getMessageKey());
           String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), btslMessage.getMessageKey(), arr);
           response.setMessage(resmsg);
            
            
        }
        catch (BTSLBaseException be) {
       	 log.error(methodName, "Exception:e=" + be);
         log.errorTrace(methodName, be);
        // hsr.setStatus(400);
          String resmsg  = RestAPIStringParser.getMessage(new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), be.getMessage(), messageArray);
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
            log.error(methodName, "Exception:e=" + e);
            log.errorTrace(methodName, e);
         	  response.setStatus(PretupsI.RESPONSE_FAIL.toString());
         	  response.setMessageCode("error.general.processing");
         	  response.setMessage("Due to some technical reasons, your request could not be processed at this time. Please try later");	
        } finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("UserDeleteController#deleteUser");
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
}

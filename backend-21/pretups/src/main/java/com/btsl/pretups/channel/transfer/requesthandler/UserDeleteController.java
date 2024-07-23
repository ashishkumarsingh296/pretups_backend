package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserDeletionBL;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.ExtUserDAO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;

/**
 * @description : This controller class will be used to process the delete
 *              request for user through external system via operator receiver.
 * @author : diwakar
 */

public class UserDeleteController implements ServiceKeywordControllerI {
    private final Log _log = LogFactory.getLog(UserDeleteController.class.getName());
    private ChannelUserVO _channelUserVO = null;
    private UserDAO _userDAO = null;
    private ChannelUserDAO _channelUserDao = null;
    private ChannelUserVO modifiesChannelUserVO = null;
    private ChannelUserVO _senderVO = null;

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param p_requestVO
     */
    @Override
	public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("UserDeleteController process", "Entered p_requestVO=" + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        HashMap requestMap = p_requestVO.getRequestMap();
        _channelUserDao = new ChannelUserDAO();
        _channelUserVO = new ChannelUserVO();
        _userDAO = new UserDAO();
        OperatorUtilI operatorUtili = null;
        final String msg[] = new String[1];
        Locale locale = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDeleteController[process]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            long balance = 0;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
            _channelUserDao = new ChannelUserDAO();
            requestMap = p_requestVO.getRequestMap();

            /*
             * Validation for Channel ADMIN or Channel user who requesting user
             * request.if BCU is the category code i.e. User is Channel admin
             * OPERATOR_CATEGORY and External Code exists, msisdn and sms pin is
             * valid or not.
             */
            final String userMsisdn = (String) requestMap.get("USERMSISDN");// User
            // MSISDN

            // Requester Validation Ends
            // Load details of channel user to be modified
            locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            final String userExtCode = null;
            final String userExtNwCode = (String) requestMap.get("EXTNWCODE");// 04-MAR-2014
            String modifiedUserLoginId = (String) requestMap.get("USERLOGINID"); // 04-MAR-2014

            // channelTransferVO.setNetworkCode(userExtNwCode);
            if(!BTSLUtil.isNullString(modifiedUserLoginId)){
            modifiedUserLoginId = modifiedUserLoginId.trim();
            }
            // Load Channel User on basis of either parameter MSISDN, Login Id
            modifiesChannelUserVO = new ExtUserDAO().loadChannelUserDetailsByMsisdnLoginIdExt(con, userMsisdn, modifiedUserLoginId, null, userExtCode, locale);
            if (modifiesChannelUserVO == null || !userExtNwCode.equalsIgnoreCase(modifiesChannelUserVO.getNetworkID().toString())) {
                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXTSYS_REQ_RECEIVER_DETAILS_INVALID); // 21-02-2014
            }

            String forceDelete=(String) requestMap.get("FORCEDELETE");
            if(_log.isDebugEnabled())
            {
            	_log.debug(METHOD_NAME+" Force Delete :", forceDelete);
            }
            if(!BTSLUtil.isNullString(forceDelete) && forceDelete.equals(PretupsI.NO))
            {
            	int pref;
                pref=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_USR_LAST_ACTIVE_TXN))).intValue();
                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            	ArrayList<UserBalancesVO> userBal = null;
            	 UserBalancesVO userBalancesVO = null;
            	 boolean dFlag = true;
            	userBal = channelUserDAO.loadUserBalances(con, modifiesChannelUserVO.getNetworkID(),modifiesChannelUserVO.getNetworkID(),modifiesChannelUserVO.getUserID());            	
            	Iterator<UserBalancesVO> itr = userBal.iterator();
                itr = userBal.iterator();
                while (itr.hasNext()) {
                    userBalancesVO = itr.next();
                    Date lastTransferOn=userBalancesVO.getLastTransferOn();
                    Date currDate=new Date();
                    int diff=BTSLUtil.getDifferenceInUtilDates(lastTransferOn,currDate);
                    if(diff<=pref)
                    {
                    	dFlag = false;
                    	break;
                    }
                }
                if(!dFlag)
                {
                	final String args[]={String.valueOf(pref)};
                	p_requestVO.setMessageArguments(args);
                	p_requestVO.setMessageCode(PretupsErrorCodesI.USER_ACTIVE);
                	throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_ACTIVE,0,p_requestVO.getMessageArguments(),null);
                }
            }
            
           
            if(_senderVO.getUserID().equals(modifiesChannelUserVO.getUserID())){
            	throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.USER_CANNOT_SELF_DELETE);
            }
            final String message = null;
            // Setting some use full parameter
            final Date currentDate = new Date();
            _channelUserVO = modifiesChannelUserVO;// Assigned VO
            _channelUserVO.setModifiedOn(currentDate);
            _channelUserVO.setModifiedBy(_senderVO.getUserID());
            _channelUserVO.setLoginID(modifiesChannelUserVO.getLoginID());
            String existingUserMSISDN=modifiesChannelUserVO.getMsisdn();
           // String parentCatCode = _userDAO.userCategoryFromMSISDN(con, existingUserMSISDN);
            String parentCatCode = _userDAO.userCategoryFromMSISDNforDelete(con, existingUserMSISDN);
            if (BTSLUtil.isNullString(parentCatCode)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_PARENT_CAT_NOT_ALLOWED);
            }
            String remarks = (String) requestMap.get("REMARKS");
            if (!BTSLUtil.isNullString(remarks)) {
                remarks = remarks.trim();
                _channelUserVO.setRemarks(remarks);
            }

            // Logic to process delete based on action Id
            // 1-Delete
            _channelUserVO.setPreviousStatus(modifiesChannelUserVO.getStatus());//
            _channelUserVO.setModifiedOn(currentDate);
            _channelUserVO.setModifiedBy(_senderVO.getUserID());
            // out Suspend
            // if(BTSLUtil.isNullString(modifiesChannelUserVO.getOutSuspened()))
            UserDeletionBL.validateForDelete(con, _channelUserVO);// Validation
            // for delete

            // 6.5
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_DLT_APP))).booleanValue()) {
                _channelUserVO.setStatus(PretupsI.USER_STATUS_DELETE_REQUEST);
            } else {
                _channelUserVO.setStatus(PretupsI.STATUS_DELETE);// Delete
                // process
                if (_userDAO.isUserBalanceExist(con, _channelUserVO.getUserID())) {
                    ChannelUserVO fromChannelUserVO = new ChannelUserVO();
                    final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                    final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
                    ArrayList<UserBalancesVO> userBal = null;
                    UserBalancesVO userBalancesVO = null;
                    fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, _channelUserVO.getUserID(), false, currentDate,false);
                    fromChannelUserVO.setGateway(p_requestVO.getRequestGatewayCode());
                    // to load the balances(multi-product and multi-wallet) of
                    // the user to be deleted
                    userBal = userBalancesDAO.loadUserBalanceForDelete(con, fromChannelUserVO.getUserID());// user
                    // to
                    // be
                    // deleted
                    // ProductVO productVO=null;
                    Iterator<UserBalancesVO> itr = userBal.iterator();
                    itr = userBal.iterator();
                    boolean sendMsgToOwner = false;
                    long totBalance = 0;
                    while (itr.hasNext()) {
                        userBalancesVO = itr.next();
                        if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() && !(("ROOT").equals(_channelUserVO
                            .getParentID()))) {
                        	final ChannelUserVO toChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, fromChannelUserVO.getOwnerID(), false, currentDate,false);
                        	if(!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(toChannelUserVO.getStatus()))
                        	{
                        	UserDeletionBL.updateBalNChnlTransfersNItemsC2C(con, fromChannelUserVO, toChannelUserVO, _senderVO.getUserID(), p_requestVO.getRequestGatewayCode(),
                                userBalancesVO);
                            sendMsgToOwner = true; 
                            totBalance += userBalancesVO.getBalance();
                        	}
                        	else
                        		throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PARENT_USER_SUSPENDED);	
                        		
                        } else {
                            UserDeletionBL.updateBalNChnlTransfersNItemsO2C(con, fromChannelUserVO, _senderVO, p_requestVO.getRequestGatewayCode(), p_requestVO
                                .getRequestGatewayType(), userBalancesVO);
                        }
                        balance += userBalancesVO.getBalance();
                    }
                    //ASHU
                    if(sendMsgToOwner) {
                    	    ChannelUserVO channelUserVO = new ChannelUserDAO().loadChannelUserByUserID(con, fromChannelUserVO.getOwnerID());
                 	        String msgArr [] = {fromChannelUserVO.getMsisdn(),PretupsBL.getDisplayAmount(totBalance)};
                 	    	final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
                            final PushMessage pushMessageToOwner = new PushMessage(channelUserVO.getMsisdn(), sendBtslMessageToOwner, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
                                           (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), fromChannelUserVO.getNetworkID());
                           pushMessageToOwner.push();    
                    	
                    }
                    
                }

            }
            ArrayList List=new ArrayList();
            // update in to user table
            List.add(_channelUserVO);
            final int userCount = new UserDAO().deleteSuspendUser(con, List);
            if (userCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "ChannelUserDeleteSuspendResumeRequestHandler[process]", "", "", "", "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_SUS_RES_FAILED);
            }
            // update data into channel users table
            final int userChannelCount = _channelUserDao.updateChannelUserInfo(con, _channelUserVO);
            if (userChannelCount <= 0) {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "ChannelUserDeleteSuspendResumeRequestHandler[process]", "", "", "", "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXTSYS_REQ_USR_SUS_RES_FAILED);
            }
            con.commit();
            requestMap.put("CHNUSERVO", _channelUserVO);
            p_requestVO.setRequestMap(requestMap);
            BTSLMessages btslPushMessage = null;
            _channelUserVO.setMsisdn(modifiesChannelUserVO.getMsisdn());
            // send SMS
            if (!BTSLUtil.isNullString(_channelUserVO.getMsisdn())) {
                if ((PretupsI.USER_STATUS_DELETE_REQUEST).equals(_channelUserVO.getStatus())) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.USER_DELETE_REQUEST_SUCCESS);
                } else {
                    if (balance > 0) {
                        final String[] arr = { PretupsBL.getDisplayAmount(balance) };
                        p_requestVO.setMessageArguments(arr);
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USER_DELETE_SUCCESS);
                    } else {
                        p_requestVO.setMessageCode(PretupsErrorCodesI.USER_DELETED_SUCCESS);
                    }
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
                    final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, null, null, locale, _channelUserVO.getNetworkID(),
                        "Related SMS will be delivered shortly");
                    pushMessage.push();
                }
            }
            if(!BTSLUtil.isNullString(_senderVO.getMsisdn())){
            	p_requestVO.setMessageSentMsisdn(_senderVO.getMsisdn());
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDeleteController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            _channelUserDao = null;
            _userDAO = null;
            _channelUserVO = null;


			if (mcomCon != null) {
				mcomCon.close("UserDeleteController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }

}

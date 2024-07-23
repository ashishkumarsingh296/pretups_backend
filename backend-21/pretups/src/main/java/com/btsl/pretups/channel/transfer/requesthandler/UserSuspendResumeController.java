package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
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
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;

/**
 * @description : This controller class will be used to process the
 *              suspend/resume request for user through external system via
 *              operator receiver.
 * @author : diwakar
 */
public class UserSuspendResumeController implements ServiceKeywordControllerI {
    private Log log = LogFactory.getLog(UserSuspendResumeController.class.getName());
    private ChannelUserVO _channelUserVO = null;
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
        final String methodName = "process";
        LogFactory.printLog("UserSuspendResumeController"+methodName, "Entered p_requestVO=" + p_requestVO, log);
       
        Connection con = null;MComConnectionI mcomCon = null;
        HashMap requestMap = p_requestVO.getRequestMap();
        _channelUserDao = new ChannelUserDAO();
        _channelUserVO = new ChannelUserVO();
        OperatorUtilI operatorUtili = null;
        final String msg[] = new String[1];
        Locale locale = null;
        try {
            final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserSuspendResumeController[process1]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }

        try {
            _senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
            mcomCon = new MComConnection();con=mcomCon.getConnection();

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

            String userExtCode = (String) requestMap.get("EXTERNALCODE");
            String modifiedUserLoginId = (String) requestMap.get("USERLOGINID");
            if(!BTSLUtil.isNullString(userExtCode))
               userExtCode = userExtCode.trim();
        	 
            if(!BTSLUtil.isNullString(modifiedUserLoginId))
            	modifiedUserLoginId = modifiedUserLoginId.trim();
            
           // Load Channel User on basis of either parameter MSISDN, Login Id
            modifiesChannelUserVO = new ExtUserDAO().loadChannelUserDetailsByMsisdnLoginIdExt(con, userMsisdn, modifiedUserLoginId, null, userExtCode, locale);

            if (modifiesChannelUserVO == null) {
                throw new BTSLBaseException(this, "loadValidateUserDetails", PretupsErrorCodesI.EXTSYS_REQ_RECEIVER_DETAILS_INVALID);
            }

            final String message = null;
            // Setting some use full parameter
            final Date currentDate = new Date();
            _channelUserVO = modifiesChannelUserVO;// Assigned VO
            _channelUserVO.setModifiedOn(currentDate);
            _channelUserVO.setModifiedBy(_senderVO.getUserID());
            _channelUserVO.setLoginID(modifiesChannelUserVO.getLoginID());

            log.debug("UserSuspendResumeController"+methodName, " p_requestMap = " + requestMap);
            String actionId = (String) requestMap.get("ACTION");
            actionId = actionId.trim();

            if (actionId.equalsIgnoreCase("S")) {
                // Logic to process suspend based on action Id
                // 1-Suspend
                _channelUserVO.setPreviousStatus(modifiesChannelUserVO.getStatus());// Suspend
                // process
                _channelUserVO.setModifiedOn(currentDate);
                _channelUserVO.setModifiedBy(_senderVO.getUserID());
                validateForSuspend(_channelUserVO);// Validation for
                // Suspend
                _channelUserVO.setStatus(PretupsI.STATUS_SUSPEND);// Suspend
                // User
            } else if (actionId.equalsIgnoreCase("R")) {
                // Logic to process resume based on action Id
                // 1-Resume
                _channelUserVO.setPreviousStatus(modifiesChannelUserVO.getStatus());//
                _channelUserVO.setModifiedOn(currentDate);
                _channelUserVO.setModifiedBy(_senderVO.getUserID());
                validateForResume(_channelUserVO);// Validation for Resume
                _channelUserVO.setStatus(PretupsI.STATUS_ACTIVE);// Resume
                // process
            }
            // update in to user table
            final int userCount = new UserDAO().updateUser(con, _channelUserVO);
            if (userCount <= 0) {
            	mcomCon.finalRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "ChannelUserDeleteSuspendResumeRequestHandler["+methodName+"]", "", "", "", "Exception:Update count <=0 ");
                throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SUS_RES_FAILED);
            }
            // update data into channel users table
            final int userChannelCount = _channelUserDao.updateChannelUserInfo(con, _channelUserVO);
            if (userChannelCount <= 0) {
            	mcomCon.finalRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR,
                    "ChannelUserDeleteSuspendResumeRequestHandler["+methodName+"]", "", "", "", "Exception:Update count <=0 ");
                throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SUS_RES_FAILED);
            }
            mcomCon.finalCommit();
            requestMap.put("CHNUSERVO", _channelUserVO);
            p_requestVO.setRequestMap(requestMap);

            // Incase if msisdn is null
            if (actionId.equalsIgnoreCase("S")) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_SUSPEND_SUCCESS);
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_RESUMED_SUCCESS);
            }
            // ended here
            BTSLMessages btslPushMessage = null;
            // send SMS
            if (!BTSLUtil.isNullString(_channelUserVO.getMsisdn())) {
                if (actionId.equalsIgnoreCase("S")) {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.USER_SUSPEND_SUCCESS);
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_SUSPEND_SUCCESS);
                    final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, null, null, locale, _channelUserVO.getNetworkID(),
                        "Related SMS will be delivered shortly");
                    pushMessage.push();
                } else {
                    p_requestVO.setMessageCode(PretupsErrorCodesI.USER_RESUMED_SUCCESS);
                    btslPushMessage = new BTSLMessages(PretupsErrorCodesI.USER_RESUMED_SUCCESS);
                    final PushMessage pushMessage = new PushMessage(_channelUserVO.getMsisdn(), btslPushMessage, null, null, locale, _channelUserVO.getNetworkID(),
                        "Related SMS will be delivered shortly");
                    pushMessage.push();
                }
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);

            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "BTSLBaseException " + be.getMessage());
            log.errorTrace(methodName, be);
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
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.errorTrace(methodName, ee);
            }
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserSuspendResumeController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            _channelUserDao = null;
            _channelUserVO = null;

			if (mcomCon != null) {
				mcomCon.close("UserSuspendResumeController#process");
				mcomCon = null;
			}
           LogFactory.printLog(methodName, " Exited ", log);
           
        }
    }

    // Validate for Suspend
    /**
     * @description : method check validation for suspension case of channel
     *              user
     * @param p_con
     * @param p__channelUserVO
     * @throws BTSLBaseException
     */
    private void validateForSuspend(ChannelUserVO p__channelUserVO) throws BTSLBaseException {
    	final String methodName = "validateForSuspend";
        if ((PretupsI.STATUS_SUSPEND).equals(_channelUserVO.getStatus())) {
            // if
            // currently
            // suspended
            throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ALREADY_SUSPENDED);
        } else if ((_channelUserVO.getStatus()).equals(PretupsI.STATUS_CANCELED) || (_channelUserVO.getStatus()).equals(PretupsI.STATUS_DELETE)) {
            throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.EXTSYS_REQ_USER_NOT_EXIST);
        }
    }

    // Validate for Resume
    /**
     * @description : Validation checks for resume
     * @param p_con
     * @param p__channelUserVO
     * @throws BTSLBaseException
     */
    private void validateForResume(ChannelUserVO p__channelUserVO) throws BTSLBaseException {
    	final String methodName = "validateForResume";
        if ((PretupsI.STATUS_ACTIVE).equals(_channelUserVO.getStatus())) {
            // if
            // currently
            // active
            throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_ALREADY_ACTIVE);
        } else if ((_channelUserVO.getStatus()).equals(PretupsI.STATUS_CANCELED) || (_channelUserVO.getStatus()).equals(PretupsI.STATUS_DELETE)) {
            throw new BTSLBaseException(this,methodName, PretupsErrorCodesI.EXTSYS_REQ_USER_NOT_EXIST);
        }
    }
}

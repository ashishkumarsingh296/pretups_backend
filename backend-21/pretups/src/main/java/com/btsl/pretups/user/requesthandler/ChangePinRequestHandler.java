package com.btsl.pretups.user.requesthandler;

/**
 * @(#)ChangePinRequestHandler.java
 *                                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 *                                  this class use for changing the pin of SMS
 *                                  USER
 *                                  <description>
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  manoj kumar june 22, 2005 Initital Creation
 *                                  Gurjeet Singh Bedi Dec 03,2005 Modified for
 *                                  PIN position changes
 *                                  Santanu Mohanty Dec 05,2007 Modified for PIN
 *                                  History Management
 *                                  Santanu Mohanty March 19,2008 Modified for
 *                                  PIN Rule Management
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 * 
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;
import com.web.user.businesslogic.UserWebDAO;

public class ChangePinRequestHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ChangePinRequestHandler.class.getName());
    private static OperatorUtilI _operatorUtil = null;

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinController[initialize]", "", "", "",
                            "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }

		Connection con = null;
		MComConnectionI mcomCon = null;
        UserPhoneVO userPhoneVO = null;
        ChannelUserVO channelUserVO = null;
        try {
            final ChannelUserVO userVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!userVO.isStaffUser()) {
                channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            } else {
                channelUserVO = ((ChannelUserVO) p_requestVO.getSenderVO()).getStaffUserDetails();
            }
            userPhoneVO = channelUserVO.getUserPhoneVO();

            // <Key Word> <OLD_PIN> <NEW_PIN> <CONFIRM_NEW_PIN>

            String[] messageArr = _operatorUtil.getC2SChangePinMessageArray(p_requestVO.getRequestMessageArray());
            String binaryMessgeArray[] = new String[messageArr.length];
            binaryMessgeArray = messageArr;
            final String[] newMessageArr = new String[messageArr.length];

            final String modifificationType = PretupsI.USER_PIN_MANAGEMENT;
            final UserDAO userDAO = new UserDAO();
            if (!p_requestVO.isPlainMessage()) {
                newMessageArr[0] = binaryMessgeArray[0];
                newMessageArr[1] = binaryMessgeArray[3];
                newMessageArr[2] = binaryMessgeArray[1];
                newMessageArr[3] = binaryMessgeArray[2];
                messageArr = newMessageArr;
            }
            /**
             * Note: checks 1.) message should be in the mentioned format
             * <KeyWord> <OLD_PIN> <NEW_PIN> <CONFIRM_NEW_PIN>
             * 2.) old pin and previously registered PIN both should be same
             * 3.) old pin and new pin should not same
             * 4.) new pin should be numeric
             * 5.) pin length should be same as defined in the system
             * 6.) new pin and confirm pin should be same
             */
            if (messageArr.length == PretupsI.MESSAGE_LENGTH_CHANGE_PIN) {
                if (!BTSLUtil.isNullString(messageArr[2])) {

					mcomCon = new MComConnection();
					con = mcomCon.getConnection();
                    userPhoneVO.setForcePinCheckReqd(false);
                  
                    if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                        if (BTSLUtil.isNullString(messageArr[1])) {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_CPIN_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                                            .getActualMessageFormat() }, null);
                        } else {
                            try {
                                ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), messageArr[1]);
                            } catch (BTSLBaseException be) {
                                _log.errorTrace(METHOD_NAME, be);
                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                                .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                                    con.commit();
                                }
                                throw be;
                            }
                        }
                    }

                    if (!((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)).equals(messageArr[2])) // C2S
                    // users
                    // not
                    // allowed
                    // to
                    // set
                    // default
                    // PIN
                    // as
                    // discussed
                    // with
                    // sanjay
                    // sir
                    {
                        if (!messageArr[2].equals(messageArr[1])) {
                            try {
                                _operatorUtil.validatePINRules(messageArr[2]);
                            } catch (BTSLBaseException be) {
                                _log.errorTrace(METHOD_NAME, be);
                                if (be.isKey()) {
                                    if (PretupsErrorCodesI.PIN_LENGTHINVALID.equals(be.getMessageKey())) {
                                        final String[] lenArr = new String[2];
                                        lenArr[0] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MIN_LENGTH))).intValue());
                                        lenArr[1] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_PIN_MAX_LENGTH))).intValue());
                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_LENGTHINVALID, 0, lenArr, null);
                                    } else if (PretupsErrorCodesI.NEWPIN_NOTNUMERIC.equals(be.getMessageKey())) {
                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_NEWPIN_NOTNUMERIC);
                                    } else if (PretupsErrorCodesI.PIN_SAMEDIGIT.equals(be.getMessageKey())) {
                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_SAMEDIGIT);
                                    } else if (PretupsErrorCodesI.PIN_CONSECUTIVE.equals(be.getMessageKey())) {
                                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CONSECUTIVE);
                                    } else {
                                        throw be;
                                    }
                                }
                            }
                           
                            if (messageArr[2].equals(messageArr[3])) {
                                final Date currentDate = new Date();
                                channelUserVO.setModifiedOn(currentDate);
                                if(!BTSLUtil.isNullString(channelUserVO.getActiveUserID())){
                                	channelUserVO.setModifiedBy(channelUserVO.getActiveUserID());
                                }
                                
                                final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

                                // check if new pin already exist in history
                                // table
                                final boolean pinStatus = userDAO.checkPasswordHistory(con, modifificationType, channelUserVO.getActiveUserID(), userPhoneVO.getMsisdn(),
                                                BTSLUtil.encryptText(messageArr[2]));
                                if (pinStatus) {
                                    final String[] lenArr = new String[2];
                                    lenArr[0] = String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue());
                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHECK_HISTORY_EXIST, 0, lenArr, null);
                                }
                                final Calendar cal = BTSLDateUtil.getInstance();
                                cal.setTime(userPhoneVO.getPinModifiedOn());
                                final int resetPinExpiredInHours = ((Integer) PreferenceCache.getControlPreference(PreferenceI.RESET_PIN_EXPIRED_TIME_IN_HOURS, channelUserVO
                                                .getNetworkID(), channelUserVO.getCategoryCode())).intValue();
                                cal.add(Calendar.HOUR, resetPinExpiredInHours);
                                final Date resetPinExpiredTime = cal.getTime();

                                int count = 0;
                                int remarksCount=0;
                                if (PretupsI.STATUS_ACTIVE.equals(channelUserVO.getPinReset()) && p_requestVO.getCreatedOn().after(resetPinExpiredTime)) {
                                    final String[] arr = { resetPinExpiredTime.toString() };
                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_RESET_PIN_EXPIRED, arr);
                                } else {
                                    count = channelUserDAO.changePin(con, messageArr[2], channelUserVO);
                                }

                                if (count > 0) {
                                	if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue())
                      	           { 
                                		if(BTSLUtil.isNullString(p_requestVO.getRemarks())){

                                			channelUserVO.setRemarks(PretupsI.SYSTEM);
                                		}else{
                                		channelUserVO.setRemarks(p_requestVO.getRemarks());
                                		}
                                	UserEventRemarksVO userRemarskVO=null;
                   					ArrayList<UserEventRemarksVO> changePinRemarks=null;
                   					if(channelUserVO!=null)
                   		    		   {
                   		    			   
                   		    			   changePinRemarks=new ArrayList<UserEventRemarksVO>();
                   	                  	userRemarskVO=new UserEventRemarksVO();
                   	                  	userRemarskVO.setCreatedBy(channelUserVO.getCreatedBy());
                   	                  	userRemarskVO.setCreatedOn(new Date());
                   	                  	userRemarskVO.setEventType(PretupsI.CHANGE_PIN);
                   	                  	userRemarskVO.setRemarks(channelUserVO.getRemarks());
                   	                  	userRemarskVO.setMsisdn(channelUserVO.getMsisdn());
                   	                  	userRemarskVO.setUserID(channelUserVO.getUserID());
                   	                  	userRemarskVO.setUserType("CHANNEL");
                   	                  	userRemarskVO.setModule(PretupsI.C2S_MODULE);
                   	                  	changePinRemarks.add(userRemarskVO);
                   	                 remarksCount=new UserWebDAO().insertEventRemark(con, changePinRemarks);
                   		    		   }
                      	           
                                	if(remarksCount>0){
                                    con.commit();
                                    // set the argument which will be send to
                                    // user as SMS part
                                    final String[] arr = { messageArr[2] };
                                    p_requestVO.setMessageArguments(arr);
                                    p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_PIN_CHANGE_SUCCESS);
                                    return;
                   		    		   }
                                     else{
                                		 con.rollback();
                                         throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHANGE_FAILED);
                                	}
                      	           }
                                	else{
                                		con.commit();
                                        // set the argument which will be send to
                                        // user as SMS part
                                        final String[] arr = { messageArr[2] };
                                        p_requestVO.setMessageArguments(arr);
                                        p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_PIN_CHANGE_SUCCESS);
                                        return;	
                                	}
                                } else {
                                    con.rollback();
                                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_CHANGE_FAILED);
                                }
                            } else {
                                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_NEWCONFIRMNOTSAME);
                            }
                   
                        } else {
                            throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_OLDNEWSAME);
                        }
                    } else {
                        final String[] arr = { ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)) };
                        throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_PIN_SAME_TO_DEFAULT_PIN, 0, arr, null);
                    }
                } else {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_PIN_BLANK);
                }
            } else {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_CPIN_INVALIDMESSAGEFORMAT, 0,
                                new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
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
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangePinRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChangePinRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }
}

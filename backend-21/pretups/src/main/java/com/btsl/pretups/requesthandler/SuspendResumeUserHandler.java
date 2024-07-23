package com.btsl.pretups.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * @(#)SuspendResumeUserHandler.java
 *                                   Copyright(c) 2006-2007, Bharti Telesoft
 *                                   Ltd.
 *                                   All Rights Reserved
 * 
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Ankit Singhal Apr 13, 2007 Initial Creation
 * 
 *                                   This class parses the request received on
 *                                   the basis of format for the SUSPENDCUSER
 *                                   (Suspend or resume User).
 */

public class SuspendResumeUserHandler implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(SuspendResumeUserHandler.class.getName());
    private HashMap _requestMap = null;
    private RequestVO _requestVO = null;
    private String _msisdn = null;
    private String _userLoginId = null;
    private String _action = null;
	private static  final String XML_TAG_MSISDN="MSISDN";
	private static final String XML_TAG_USERLOGINID="USERLOGINID";
    private static final String XML_TAG_ACTION = "ACTION";
    private static final String PIN = "PIN";

    /**
     * This method is the entry point into the class.
     * method process
     * 
     * @param RequestVO
     */

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered....: p_requestVO= " + p_requestVO.toString());
        }
        _requestVO = p_requestVO;
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = null;
        boolean messageRequired = false;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();

            // retreiving the Map which has all the values in the request.These
            // values can be retreived by
            // using the tag name
            _requestMap = _requestVO.getRequestMap();
            _msisdn = (String) _requestMap.get(XML_TAG_MSISDN);
            _userLoginId = (String) _requestMap.get(XML_TAG_USERLOGINID);
            _action = (String) _requestMap.get(XML_TAG_ACTION);
            // _action="S";

            // this method validates whether the msisdn, module and action have
            // valid values in the request
            channelUserVO = validate(con);
            if(!(channelUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE))){
            	
            	throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.REQVIASMS_CHANNEL_USER_NOT_EXIST);
            }
            ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (senderVO != null && channelUserVO != null) {
                UserPhoneVO userPhoneVO = null;
                if (!senderVO.isStaffUser()) {
                    userPhoneVO = senderVO.getUserPhoneVO();
                } else {
                    userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
                }
               /* if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, ((ChannelUserVO) p_requestVO.getSenderVO()), (String) _requestMap.get(PIN));
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                            con.commit();
                        }
                        throw be;
                    }
                }*/
                if (_log.isDebugEnabled()) {
                    _log.debug("process", "Sender UserID()= " + senderVO.getUserID() + ", channel User ParentID()=" + channelUserVO.getParentID() + ", Channel User OwnerID()=" + channelUserVO.getOwnerID());
                }
               /* if ((channelUserVO.getParentID().equalsIgnoreCase(senderVO.getUserID()) || channelUserVO.getOwnerID().equalsIgnoreCase(senderVO.getUserID()))) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.NO_USER_EXIST);
                } else {*/
                    messageRequired = true;
                }
           // }
            suspendResumeUser(con, channelUserVO);

            // setting the transaction status to true
            _requestVO.setSuccessTxn(true);
            if (messageRequired) {
                String key = null;
                UserPhoneVO receiverPhoneVO = channelUserVO.getUserPhoneVO();
                if (receiverPhoneVO == null) {
                    receiverPhoneVO = new UserDAO().loadUserAnyPhoneVO(con, channelUserVO.getMsisdn());
                } else if (receiverPhoneVO.getPhoneLanguage()==null) {
                    receiverPhoneVO =new UserDAO().loadUserAnyPhoneVO(con,channelUserVO.getMsisdn());
				}
                String[] arrMsg = { senderVO.getMsisdn(), receiverPhoneVO.getMsisdn() };
                p_requestVO.setMessageArguments(arrMsg);
                if (PretupsI.USER_STATUS_ACTIVE.equalsIgnoreCase(_action)) {
                    key = PretupsErrorCodesI.CCE_USER_STATUS_RESUME_SUCCESS_R;
                    _requestVO.setMessageCode(PretupsErrorCodesI.CCE_USER_STATUS_RESUME_SUCCESS_S);
                } else {
                    if (PretupsI.USER_STATUS_SUSPEND_REQUEST.equals(_action)) {
                        key = PretupsErrorCodesI.CCE_USER_STATUS_SUSPEND_REQUEST_R;
                        _requestVO.setMessageCode(PretupsErrorCodesI.CCE_USER_STATUS_SUSPEND_REQUEST_S);
                    } else {
                        key = PretupsErrorCodesI.CCE_USER_STATUS_SUSPEND_SUCCESS_R;
                        _requestVO.setMessageCode(PretupsErrorCodesI.CCE_USER_STATUS_SUSPEND_SUCCESS_S);
                    }
                }
                PushMessage pushMessage = null;
                Locale locale1 = new Locale(receiverPhoneVO.getPhoneLanguage(), receiverPhoneVO.getCountry());
                String senderMessage = BTSLUtil.getMessage(locale1, key, arrMsg);
               pushMessage = new PushMessage(receiverPhoneVO.getMsisdn(), senderMessage, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), locale1);
               //Push Message Removed for Duplicate Message 
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        } catch (Exception ex) {
            _requestVO.setSuccessTxn(false);

            // Rollbacking the transaction
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "Exception " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendResumeUserHandler[process]", "", "", "", "Exception:" + ex.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        }// end of Exception
        finally {
            _requestVO.setRequestMap(_requestMap);
            p_requestVO = _requestVO;
            // clossing database connection
			if (mcomCon != null) {
				mcomCon.close("SuspendResumeUserHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }// end of finally
    }

    /**
     * This method is called to validate the values present in the requestMap of
     * the requestVO
     * The purpose of this method is to validate the values of the msisdn,
     * userLoginId, action
     * 
     * @param p_con
     *            Connection
     * @return channelUserVO ChannelUserVO
     * @throws BTSLBaseException
     */
    private ChannelUserVO validate(Connection p_con) throws BTSLBaseException {
        final String METHOD_NAME = "validate";
        if (_log.isDebugEnabled()) {
            _log.debug("validate", "Entered.....");
        }

        String arr[] = null;
        ChannelUserVO channelUserVO = null;
        ChannelUserDAO channelUserDAO = null;
        String filteredMsisdn = null;
        try {
            String status = "'" + PretupsI.USER_STATUS_ACTIVE + "'" + "," + "'" + PretupsI.USER_STATUS_SUSPEND + "','" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
            String statusUsed = PretupsI.STATUS_IN;
            channelUserDAO = new ChannelUserDAO();

            if (BTSLUtil.isNullString(_action)) {
                _requestMap.put("RES_ERR_KEY", XML_TAG_ACTION);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }
            if (PretupsI.USER_STATUS_ACTIVE_R.equalsIgnoreCase(_action)) {
                _action = PretupsI.USER_STATUS_ACTIVE;
            }

            if (!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(_action) && !PretupsI.USER_STATUS_ACTIVE.equalsIgnoreCase(_action)) {
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_ACTION_VALUE);
            }

            if (!BTSLUtil.isNullString(_msisdn)) {
                // filtering the msisdn for country independent dial format
                filteredMsisdn = PretupsBL.getFilteredMSISDN(_msisdn);
                if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                    _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
                }
                // load user details by msisdn
                channelUserVO = channelUserDAO.loadUsersDetails(p_con, filteredMsisdn, null, statusUsed, status);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4MSISDN);
                }
                if (!BTSLUtil.isNullString(_userLoginId) && !channelUserVO.getLoginID().equalsIgnoreCase(_userLoginId)) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_INVALID_LOGIN_ID_FOR_MSISDN);
                }
            } else if (!BTSLUtil.isNullString(_userLoginId)) {
                // load user details by login id
                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(p_con, _userLoginId, null, statusUsed, status);
                if (channelUserVO == null) {
                    throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_CU_DETAILS_NOT_FOUND4LOGINID);
                }

                filteredMsisdn = PretupsBL.getFilteredMSISDN(channelUserVO.getMsisdn());

            } else {
                _requestMap.put("RES_ERR_KEY", XML_TAG_MSISDN);
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_MISSING_MANDATORY_VALUE);
            }

            String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
            _requestVO.setFilteredMSISDN(filteredMsisdn);
            // checking whether the msisdn prefix is valid in the network
            NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            if (networkPrefixVO == null) {
                arr = new String[] { filteredMsisdn };
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK, arr);
            }
            String networkCode = networkPrefixVO.getNetworkCode();
            if (networkCode != null && !networkCode.equalsIgnoreCase(((UserVO) _requestVO.getSenderVO()).getNetworkID())) {
                throw new BTSLBaseException(this, "validate", PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
            }

            return channelUserVO;
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("validate", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("validate", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendResumeUserHandler[validate]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("SuspendResumeUserHandler", "validate", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validate", "Exiting ");
            }
        }
    }

    /**
     * This method is called when a request is received for suspend or resume
     * channel user.
     * method suspendResumeUser
     * 
     * @param p_con
     */
    private void suspendResumeUser(Connection p_con, ChannelUserVO p_channelUserVO) throws BTSLBaseException {
        final String METHOD_NAME = "suspendResumeUser";
        if (_log.isDebugEnabled()) {
            _log.debug("suspendResumeUser", "Entered .....");
        }
        ArrayList userList = new ArrayList();
        UserVO userVO = new UserVO();
        try {
            // checking for user status as suspended user can be suspended and
            // resumed user cant be resumed.
            if (p_channelUserVO.getStatus().equalsIgnoreCase(_action)) {
                if (PretupsI.USER_STATUS_ACTIVE.equalsIgnoreCase(_action)) {
                    throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_ACTIVE);
                } else if (PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(_action)) {
                    throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_ERROR_USER_ALREADY_SUSPENDED);
                }
            }
            if (p_channelUserVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
                throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_USER_SUSPENDED_APPROVAL_PENDING);
            }
            p_channelUserVO.setPreviousStatus(p_channelUserVO.getStatus());

            // added for approval suspend channel user through ussd, ExtGw, sms
            // (road map 5.8)
            if (PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(_action)) {
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_SUSPENSION_APPROVAL)).booleanValue()) {
                    p_channelUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND_REQUEST);
                    _action = PretupsI.USER_STATUS_SUSPEND_REQUEST;
                }

                else {
                    p_channelUserVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
                }

            } else {
                p_channelUserVO.setStatus(_action.toUpperCase());
            }

            userList.add(p_channelUserVO);
            UserDAO userDAO = new UserDAO();
            int updtaeCount = userDAO.deleteSuspendUser(p_con, userList);

            if (updtaeCount > 0) {
                p_con.commit();
            } else {
                throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.CCE_XML_ERROR_CHANNEL_USER_NOTUPDATE);
            }
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            _log.error("suspendResumeUser", "BTSLBaseException " + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error("suspendResumeUser", "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SuspendResumeUserHandler[suspendResumeUser]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "suspendResumeUser", PretupsErrorCodesI.REQ_NOT_PROCESS);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("suspendResumeUser", "Exiting.....");
            }
        }
    }
}

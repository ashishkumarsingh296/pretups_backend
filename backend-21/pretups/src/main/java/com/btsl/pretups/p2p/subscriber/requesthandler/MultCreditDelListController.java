/**
 * @(#)MultCreditDelListController.java
 *                                      Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                      All Rights Reserved
 *                                      Controller class for deleting the
 *                                      buddylist of a subscriber
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Author Date History
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 *                                      Harsh Dixit Aug 13,2012 Initital
 *                                      Creation
 * 
 *                                      ----------------------------------------
 *                                      ----------------------------------------
 *                                      -----------------
 * 
 */

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Locale;
// import java.util.Locale;

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
// import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.btsl.pretups.p2p.subscriber.businesslogic.P2PBuddiesDAO;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

public class MultCreditDelListController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(MultCreditDelListController.class.getName());
    private static OperatorUtilI _operatorUtil = null;
    private String _sMsisdn;
    private String _buddyListName;
    private RequestVO _requestVO = null;
    private Locale _senderLocale = null;
    private Connection con = null;
    private MComConnectionI mcomCon = null;
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " MultCreditDelListController [initialize]", "", "",
                "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
     * 
     */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }
        final String methodName = "process";
        try {
            _requestVO = p_requestVO;
            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();

            // <Key Word> < Name / MSISDN> <PIN>
            // <Key Word> < Name / MSISDN>
            if (mcomCon == null) {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
            }
            final String actualPin = senderVO.getPin();
            String msisdn = senderVO.getMsisdn();
            final String[] args = p_requestVO.getRequestMessageArray();
            final int messageLength = args.length;
            _senderLocale = p_requestVO.getSenderLocale();
            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY_LIST - 1): {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                _buddyListName = null;
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY_LIST): {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                _buddyListName = args[2];
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            // to check the ListName and MSISDN which sends by user is proper or
            // not
            /*
             * if(BTSLUtil.isNullString(_buddyListName))
             * throw new BTSLBaseException(this,"process",PretupsErrorCodesI.
             * BUDDYLIST_NOT_FOUND);
             */
            msisdn = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(msisdn));
            _sMsisdn = msisdn.trim();
            // Checks If flow type is common then validation will be performed
            // before sending the
            // response to user and if it is thread based then validation will
            // also be performed in thread
            // along with topup
            if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_COMMON)) {
                System.out.println();
                // Process validation requests
                processDeletionRequest();
                p_requestVO.setSenderMessageRequired(true);
                p_requestVO.setDecreaseLoadCounters(false);
            } else if (p_requestVO.getMessageGatewayVO().getFlowType().equals(PretupsI.MSG_GATEWAY_FLOW_TYPE_THREAD)) {
                p_requestVO.setMessageCode(PretupsI.TXN_STATUS_UNDER_PROCESS);
                p_requestVO.setSenderReturnMessage(getSenderUnderProcessMessage());
                p_requestVO.setSenderMessageRequired(false);
                final Thread _controllerThread = new Thread(this);
                _controllerThread.start();
                p_requestVO.setDecreaseLoadCounters(false);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error("process", "BTSLBaseException" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.DELETE_BUDDYLIST_FAILED);
            }
            p_requestVO.setSenderMessageRequired(true);
            _log.errorTrace(methodName, be);

        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            _log.error("process", "Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultCreditDelListController[process]", "",
                ((SenderVO) p_requestVO.getSenderVO()).getMsisdn(), "", "Exception while deleting BuddyList:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("MultCreditDelListController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode())) {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            }
        }
    }

    public void run() {
        if (_log.isDebugEnabled()) {
            _log.debug("run", "Entered");
        }
        try {
            // Thread.sleep(1000);
            processDeletionRequest();
        } catch (BTSLBaseException be) {
            _log.error("run", "BTSLBaseException" + be);
        } catch (Exception e) {
            _log.error("run", "Exception" + e);
        } finally {
            final String senderMessage = BTSLUtil.getMessage(_senderLocale, _requestVO.getMessageCode(), _requestVO.getMessageArguments());
            final PushMessage pushMessage = new PushMessage(_sMsisdn, senderMessage, null, null, _senderLocale);
            pushMessage.push();
            if (_log.isDebugEnabled()) {
                _log.debug("run", "Exiting");
            }
        }
    }

    private void processDeletionRequest() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("processDeletionRequest", "Entered");
        }
        final String methodName = "processDeletionRequest";
        try {
            final P2PBuddiesDAO p2PBuddiesDAO = new P2PBuddiesDAO();
            // delete the buddy LIst
            if (mcomCon == null || con==null || con.isClosed()) {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
            }
            final int deleteCount = p2PBuddiesDAO.delMultCreditTrfList(con, _buddyListName, _sMsisdn);
            if (deleteCount > 0) {
            	mcomCon.finalCommit();
                final String arr[] = { _buddyListName };
                _requestVO.setMessageArguments(arr);
                _requestVO.setMessageCode(PretupsErrorCodesI.DELETE_BUDDYLIST_SUCCESS);
            } else {
                final String arr[] = { _buddyListName };
                _requestVO.setMessageArguments(arr);
                _requestVO.setMessageCode(PretupsErrorCodesI.BUDDYLIST_NOT_FOUND);
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.BUDDYLIST_NOT_FOUND, arr);
            }
        } catch (BTSLBaseException be) {
            _requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            _log.error("processDeletionRequest", "BTSLBaseException" + be.getMessage());
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else {
                _requestVO.setMessageCode(PretupsErrorCodesI.DELETE_BUDDYLIST_FAILED);
            }
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            _requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            _log.error("processDeletionRequest", "Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultCreditDelListController[processDeletionRequest]",
                "", ((SenderVO) _requestVO.getSenderVO()).getMsisdn(), "", "Exception while deleting BuddyList:" + e.getMessage());
            _requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("MultCreditDelListController#processDeletionRequest");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("processDeletionRequest", "Exiting");
            }
        }

    }

    private String getSenderUnderProcessMessage() {
        final String[] messageArgArray = { _buddyListName };
        return BTSLUtil.getMessage(_senderLocale, PretupsErrorCodesI.MULT_DEL_BUDDY_UNDERPROCESS, messageArgArray);
    }
}

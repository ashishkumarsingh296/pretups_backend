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

package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Locale;
// import java.util.Locale;

// import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;
import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.PreferenceCache;
import com.selftopup.pretups.preference.businesslogic.PreferenceI;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.OperatorUtilI;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

public class MultCreditDelListController implements ServiceKeywordControllerI, Runnable {
    private static Log _log = LogFactory.getLog(MultCreditDelListController.class.getName());
    public static OperatorUtilI _operatorUtil = null;
    private String _sMsisdn;
    private String _buddyListName;
    private RequestVO _requestVO = null;
    Locale _senderLocale = null;
    Connection con = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " MultCreditDelListController [initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    /**
	 * 
	 */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        try {
            _requestVO = p_requestVO;
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();

            // <Key Word> < Name / MSISDN> <PIN>
            // <Key Word> < Name / MSISDN>
            if (con == null)
                con = OracleUtil.getConnection();
            String actualPin = senderVO.getPin();
            String msisdn = senderVO.getMsisdn();
            String[] args = p_requestVO.getRequestMessageArray();
            int messageLength = args.length;
            _senderLocale = p_requestVO.getSenderLocale();
            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY_LIST - 1): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                }
                _buddyListName = null;
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_DELETE_BUDDY_LIST): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                }
                _buddyListName = args[2];
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_DELBUDDY_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
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
                Thread _controllerThread = new Thread(this);
                _controllerThread.start();
                p_requestVO.setDecreaseLoadCounters(false);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            _log.error("process", "BTSLBaseException" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.DELETE_BUDDYLIST_FAILED);
            p_requestVO.setSenderMessageRequired(true);

        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("process", "Exception" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultCreditDelListController[process]", "", ((SenderVO) p_requestVO.getSenderVO()).getMsisdn(), "", "Exception while deleting BuddyList:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (_log.isDebugEnabled())
                _log.debug("process", " Exited ");
            if (BTSLUtil.isNullString(p_requestVO.getMessageCode()))
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.REQ_NOT_PROCESS);
        }
    }

    public void run() {
        if (_log.isDebugEnabled())
            _log.debug("run", "Entered");
        try {
            // Thread.sleep(1000);
            processDeletionRequest();
        } catch (BTSLBaseException be) {
            _log.error("run", "BTSLBaseException" + be);
        } catch (Exception e) {
            _log.error("run", "Exception" + e);
        } finally {
            String senderMessage = BTSLUtil.getMessage(_senderLocale, _requestVO.getMessageCode(), _requestVO.getMessageArguments());
            PushMessage pushMessage = new PushMessage(_sMsisdn, senderMessage, null, null, _senderLocale);
            pushMessage.push();
            if (_log.isDebugEnabled())
                _log.debug("run", "Exiting");
        }
    }

    private void processDeletionRequest() throws BTSLBaseException, Exception {
        if (_log.isDebugEnabled())
            _log.debug("processDeletionRequest", "Entered");
        try {
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            // delete the buddy LIst
            if (con == null || con.isClosed()) {
                con = OracleUtil.getConnection();
            }
            int deleteCount = subscriberDAO.delMultCreditTrfList(con, _buddyListName, _sMsisdn);
            if (deleteCount > 0) {
                con.commit();
                String arr[] = { _buddyListName };
                _requestVO.setMessageArguments(arr);
                _requestVO.setMessageCode(SelfTopUpErrorCodesI.DELETE_BUDDYLIST_SUCCESS);
            } else {
                String arr[] = { _buddyListName };
                _requestVO.setMessageArguments(arr);
                _requestVO.setMessageCode(SelfTopUpErrorCodesI.BUDDYLIST_NOT_FOUND);
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.BUDDYLIST_NOT_FOUND, arr);
            }
        } catch (BTSLBaseException be) {
            _requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            _log.error("processDeletionRequest", "BTSLBaseException" + be.getMessage());
            if (be.isKey()) {
                _requestVO.setMessageCode(be.getMessageKey());
                _requestVO.setMessageArguments(be.getArgs());
            } else
                _requestVO.setMessageCode(SelfTopUpErrorCodesI.DELETE_BUDDYLIST_FAILED);

        } catch (Exception e) {
            _requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("processDeletionRequest", "Exception" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MultCreditDelListController[processDeletionRequest]", "", ((SenderVO) _requestVO.getSenderVO()).getMsisdn(), "", "Exception while deleting BuddyList:" + e.getMessage());
            _requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (_log.isDebugEnabled())
                _log.debug("processDeletionRequest", "Exiting");
        }

    }

    private String getSenderUnderProcessMessage() {
        String[] messageArgArray = { _buddyListName };
        return BTSLUtil.getMessage(_senderLocale, SelfTopUpErrorCodesI.MULT_DEL_BUDDY_UNDERPROCESS, messageArgArray);
    }
}

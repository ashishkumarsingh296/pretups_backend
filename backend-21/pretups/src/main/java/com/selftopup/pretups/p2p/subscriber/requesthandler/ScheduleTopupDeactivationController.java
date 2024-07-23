package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.sql.SQLException;

import com.selftopup.common.BTSLBaseException;
import com.selftopup.event.EventComponentI;
import com.selftopup.event.EventHandler;
import com.selftopup.event.EventIDI;
import com.selftopup.event.EventLevelI;
import com.selftopup.event.EventStatusI;
import com.selftopup.logging.Log;
import com.selftopup.logging.LogFactory;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.gateway.businesslogic.PushMessage;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsDAO;
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

public class ScheduleTopupDeactivationController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ScheduleTopupActivationController.class.getName());
    public static OperatorUtilI _operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " ScheduleTopupDeactivationController [initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }
        String _type = null;
        String _msisdn = null;
        String _imei = null;
        String _filteredMSISDN = null;
        Integer _messagelength = null;
        Connection con = null;
        int updateCount = 0;
        try {
            String[] args = (p_requestVO.getDecryptedMessage()).split("\\s");
            _msisdn = p_requestVO.getRequestMSISDN();
            _imei = args[1];
            con = OracleUtil.getConnection();
            CardDetailsDAO dao = new CardDetailsDAO();
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (senderVO == null) {
                throw new BTSLBaseException(this, "ScheduleTopupDeactivationController", SelfTopUpErrorCodesI.SUBSCRIBER_NOT_REGISTERED);
            }
            _messagelength = args.length;
            if (_messagelength == PretupsI.MESSAGE_LENGTH_DISABLE_AUTO_TOPUP) {
                // Pin validation start
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(senderVO.getPin())))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[PretupsI.MESSAGE_LENGTH_DISABLE_AUTO_TOPUP - 1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            con.commit();
                        }
                        throw be;
                    }
                }
                if (BTSLUtil.isNullString(_msisdn)) {
                    throw new BTSLBaseException(this, "ScheduleTopupDeactivationController", SelfTopUpErrorCodesI.INVALID_MSISDN_NULL);
                }
                _filteredMSISDN = PretupsBL.getFilteredMSISDN(_msisdn.trim());
                _filteredMSISDN = _operatorUtil.addRemoveDigitsFromMSISDN(_filteredMSISDN);
                if (!BTSLUtil.isValidMSISDN(_filteredMSISDN))// &&
                                                             // filteredMSISDN.equals(p_requestVO.getRequestMSISDN())
                                                             // )
                {
                    throw new BTSLBaseException(this, "ScheduleTopupDeactivationController", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                }
                if (_filteredMSISDN.equals(p_requestVO.getRequestMSISDN())) {
                    _msisdn = _filteredMSISDN;
                } else {
                    throw new BTSLBaseException(this, "ScheduleTopupDeactivationController", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                }
                boolean imeiExist = _imei.trim().equals(senderVO.getImei()) ? true : false;
                if (!imeiExist) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_IMEI);
                }
                SubscriberDAO subscriberDAO = new SubscriberDAO();
                boolean isAlreadyEnabled = false;
                try {
                    isAlreadyEnabled = subscriberDAO.checkAlreadyEnabledInDeactivation(con, senderVO.getUserID());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                if (isAlreadyEnabled) {
                    updateCount = dao.disableAutoTopUp(con, senderVO.getUserID());
                    if (updateCount > 0) {
                        _log.debug("ScheduleTopupDeactivationController process", "Successful ");
                        p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_DISABLE_SUCCESS);
                        String message1 = null;
                        message1 = BTSLUtil.getMessage(p_requestVO.getLocale(), SelfTopUpErrorCodesI.AUTO_TOPUP_DISABLE_SUCCESS, null);
                        PushMessage pushMessage1 = new PushMessage(p_requestVO.getFilteredMSISDN(), message1, p_requestVO.getRequestIDStr(), p_requestVO.getRequestGatewayCode(), p_requestVO.getLocale());
                        pushMessage1.push();

                    }
                } else {
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_DISABLE_NO_USERID);
                }
            } else {
                throw new BTSLBaseException("ScheduleTopupDeactivationController", "process", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
            }

        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.CARD_MODIFY_FAILED);
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduleTopupDeactivationController[process]", "", "", "", "Getting Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (con != null) {
                try {
                    con.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }
}

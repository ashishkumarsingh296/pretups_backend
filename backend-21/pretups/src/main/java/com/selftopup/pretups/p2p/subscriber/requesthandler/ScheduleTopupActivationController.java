package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.selftopup.pretups.p2p.transfer.businesslogic.CardDetailsVO;
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

public class ScheduleTopupActivationController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ScheduleTopupActivationController.class.getName());
    public static OperatorUtilI _operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " ScheduleTopupActivationController [initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        // //System.out.println("entered in validation of the schedule topup request ");
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }
        String _type = null;
        String _msisdn = null;
        String _imei = null;
        String _frequency = null;
        String _endDate = null;
        String _amount = null;
        String _nickName = null;
        String _filteredMSISDN = null;
        Integer _messagelength = null;
        Double _finalAmount = null;
        int _insert = 0;
        Double _tempAmt = null;
        Date _scheduleDate = null;
        Date _deactivationDate = null;
        String userID = null;
        String expiryDate = null;
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (senderVO == null) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.SUBSCRIBER_NOT_REGISTERED);
            }
            String[] args = (p_requestVO.getDecryptedMessage()).split("\\s");
            _messagelength = args.length;
            if (_messagelength == PretupsI.MESSAGE_LENGTH_AUTO_TOPUP) {
                // Pin validation start
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(senderVO.getPin())))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[PretupsI.MESSAGE_LENGTH_AUTO_TOPUP - 1]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            con.commit();
                        }
                        throw be;
                    }
                }
                // Pin validation end
                _msisdn = p_requestVO.getRequestMSISDN();
                _type = p_requestVO.getServiceType();
                _imei = args[1];
                _frequency = args[2];
                _endDate = args[6];

                if (_frequency.equalsIgnoreCase("O") || _frequency.equalsIgnoreCase("M")) {
                    _scheduleDate = BTSLUtil.getMonthlyScheduleDate(args[3]);
                } else if (_frequency.equalsIgnoreCase("W")) {
                    _scheduleDate = BTSLUtil.getWeeklyScheduleDate(args[3]);
                }

                if (!BTSLUtil.isValidDatePattern(_endDate)) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_DATE_FORMAT_ERROR);
                }
                SimpleDateFormat date_format = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                if (BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(date_format.format(_scheduleDate), PretupsI.DATE_FORMAT_DDMMYYYY), BTSLUtil.getDateFromDateString(_endDate, PretupsI.DATE_FORMAT_DDMMYYYY)) < 2) {
                    _log.error("process", "END DATE MUST BE AHEAD THAN THAT OF START DATE ");
                    String msgArr1[] = { BTSLUtil.getDateStringFromDate(_scheduleDate, PretupsI.DATE_FORMAT) };
                    p_requestVO.setMessageArguments(msgArr1);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_END_DATE_DIFF_FAILED);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_END_DATE_DIFF_FAILED, msgArr1);
                }

                _deactivationDate = BTSLUtil.getDateFromDateString(_endDate);
                _amount = args[4];
                _nickName = args[5].toUpperCase();
                // MSISDN validation start
                if (BTSLUtil.isNullString(_msisdn)) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_MSISDN_NULL);
                }
                _filteredMSISDN = PretupsBL.getFilteredMSISDN(_msisdn.trim());
                _filteredMSISDN = _operatorUtil.addRemoveDigitsFromMSISDN(_filteredMSISDN);
                if (!BTSLUtil.isValidMSISDN(_filteredMSISDN))// &&
                                                             // filteredMSISDN.equals(p_requestVO.getRequestMSISDN())
                                                             // )
                {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                }
                if (_filteredMSISDN.equals(p_requestVO.getRequestMSISDN())) {
                    _msisdn = _filteredMSISDN;
                } else {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                }
                // MSISDN validation end
                // IMEI verification
                // boolean imeiExist=
                // isImeiExist(con,_msisdn,_imei,senderVO.getUserID());
                boolean imeiExist = _imei.trim().equals(senderVO.getImei()) ? true : false;
                if (!imeiExist) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_IMEI);
                }
                // IMEI verification ends
                // now we shall check whether the row exist for this MSISDN and
                // IMEI and said NICK
                boolean oldNickExist = false;
                if (BTSLUtil.isAlphaNumeric(_nickName)) {
                    oldNickExist = isNickExist(con, _msisdn, _imei, _nickName, senderVO.getUserID());
                }
                if (!oldNickExist) {
                    _log.error("process", "PROVIDED NICK NAME DOESN'T EXIST ");
                    String msgArr1[] = { _nickName };
                    p_requestVO.setMessageArguments(msgArr1);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.INVALID_OLD_NICK);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_OLD_NICK, msgArr1);
                }
                try {
                    _tempAmt = Double.parseDouble(_amount);
                } catch (NumberFormatException nfe) {
                    _log.error("process", "Number Format Exception: Value sent can't be parse to an Int");
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                }
                // AMOUNT conversion & validation
                if (!BTSLUtil.isNullString(_amount) && BTSLUtil.isDecimalValue(_amount) && !(_tempAmt <= 0)) {
                    // _finalAmount=PretupsBL.getSystemAmount(Double.parseDouble(_amount));
                    _finalAmount = _tempAmt;
                    // _finalAmount=Integer.parseInt(_amount);
                    if (_finalAmount > (Long) SystemPreferences.MAX_AUTOTOPUP_AMT) {
                        _log.error("process", "You can't send the amount more than " + (Long) SystemPreferences.MAX_AUTOTOPUP_AMT);
                        String msgArr[] = { String.valueOf(SystemPreferences.MAX_AUTOTOPUP_AMT) };
                        p_requestVO.setMessageArguments(msgArr);
                        p_requestVO.setMessageCode(SelfTopUpErrorCodesI.MAX_AUTO_TOPUP_AMT_RCHD);
                        throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.MAX_AUTO_TOPUP_AMT_RCHD, msgArr);
                    }
                } else {
                    _log.error("process", "INVALID AMOUNT IS BEING SENT IN THE REQUEST");
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_INVALID_AMOUNT);
                }
                // AMOUNT VALIDATION END
                // check the card has expired or not before scheduling
                userID = senderVO.getUserID();
                CardDetailsVO cardDetailsVO = new CardDetailsDAO().loadCredtCardDetails(con, userID, _nickName);
                if (cardDetailsVO != null) {
                    expiryDate = BTSLUtil.decryptText(cardDetailsVO.getExpiryDate());
                } else {
                    _log.error("process", "PROVIDED NICK NAME DOESN'T EXIST ");
                    String msgArr1[] = { _nickName };
                    p_requestVO.setMessageArguments(msgArr1);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.INVALID_OLD_NICK);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_OLD_NICK, msgArr1);
                }
                date_format = new SimpleDateFormat("MM/yy");
                if (BTSLUtil.getDifferenceInUtilDates(BTSLUtil.getDateFromDateString(date_format.format(BTSLUtil.getDateFromDateString(_endDate)), "MM/yy"), BTSLUtil.getDateFromDateString(expiryDate, "MM/yy")) < 0) {
                    _log.error("process", "Card Expiry date has crossed ");
                    String msgArr1[] = { _nickName };
                    p_requestVO.setMessageArguments(msgArr1);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.CARD_EXPIRY_DATE_CROSSED);
                    throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.CARD_EXPIRY_DATE_CROSSED, msgArr1);
                }
                // checking card expiry ends

                SubscriberDAO subscriberDAO = new SubscriberDAO();
                // CHECK ALREADY ENABLED OR NOT, if not then only go for
                // registration.
                boolean isAlreadyEnabled = false;

                try {
                    isAlreadyEnabled = subscriberDAO.checkAlreadyEnabled(con, senderVO.getUserID());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                if (isAlreadyEnabled) {
                    _log.error("process", "You have already enabled the AutoTopUp ");
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_SUB_ALREADY_REG);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.AUTO_TOPUP_SUB_ALREADY_REG);
                } else {
                    try {
                        _insert = subscriberDAO.addAutoTopupUserDetails(con, senderVO.getUserID(), _type, _frequency.toUpperCase(), _finalAmount, _scheduleDate, _nickName, _deactivationDate);
                    } catch (BTSLBaseException e) {
                        _log.errorTrace("process", e);
                        throw new BTSLBaseException("ScheduleTopupActivationController", "process", SelfTopUpErrorCodesI.SCHEDULE_TOPUP_USER_PRESENT_IN_DB_WITH_N_STATUS);

                    }
                    // System.out.println("the parameters need to be entered in the db are: type:"+_type+" pin:"+
                    // _pin+" frequency:"+_frequency+" startDATE:"+_startDate+" Amount:"+
                    // _finalAmount+ " NickName:"+ _nickName+ " IMEI:"+_imei);
                    if (_insert > 0) {
                        _log.debug("ScheduleTopupActivationController process", "Successful ");
                        p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_REG_SUCCESSFUL);
                        (new PushMessage(senderVO.getMsisdn(), BTSLUtil.getMessage(senderVO.getLocale(), SelfTopUpErrorCodesI.AUTO_TOPUP_REG_SUCCESSFUL, null), "", p_requestVO.getRequestGatewayCode(), senderVO.getLocale())).push();
                        con.commit();
                    }
                }
            } else {
                throw new BTSLBaseException("ScheduleTopupActivationController", "process", SelfTopUpErrorCodesI.P2P_INVALID_MESSAGEFORMAT);
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
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.AUTO_TOPUP_REG_FAILED);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduleTopupActivationController[process]", "", "", "", "Getting Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exiting: Status:" + (_insert > 0));
            }
        }
    }

    public boolean isNickExist(Connection p_con, String p_msisdn, String p_imei, String p_nickName, String p_userID) throws BTSLBaseException, Exception {
        boolean exist = false;
        if (_log.isDebugEnabled()) {
            _log.debug("isNickExist", "Entered p_nickName:" + p_nickName + " Sender MSISDN=" + p_msisdn + "Imei:" + p_imei + " UserID:" + p_userID); /*
                                                                                                                                                      * +
                                                                                                                                                      * " p_sender Network ="
                                                                                                                                                      * +
                                                                                                                                                      * p_senderVO
                                                                                                                                                      * .
                                                                                                                                                      * getNetworkCode
                                                                                                                                                      * (
                                                                                                                                                      * )
                                                                                                                                                      * )
                                                                                                                                                      * ;
                                                                                                                                                      */
        }
        try {
            CardDetailsDAO carddetailsDAO = new CardDetailsDAO();
            exist = carddetailsDAO.checkNickName(p_con, p_msisdn, p_imei, p_nickName, p_userID);
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ScheduleTopupActivationController[isNickExist]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isNickExist", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        }
        if (_log.isDebugEnabled()) {
            _log.debug("isNickExist", "Exiting with status: " + exist);
        }
        return exist;
    }

    /*
     * public boolean isImeiExist(Connection p_con,String p_msisdn,String
     * p_imei, String p_userId) throws BTSLBaseException,Exception
     * {
     * boolean exist=false;
     * if(_log.isDebugEnabled()) {
     * _log.debug("isImeiExist","Entered : Sender MSISDN="+p_msisdn+"Imei:"+p_imei
     * +" UserId:" + p_userId) ;
     * }
     * try
     * {
     * SubscriberDAO subscriberDAO = new SubscriberDAO();
     * exist=subscriberDAO.checkImei(p_con,p_msisdn,p_imei,p_userId);
     * }
     * catch (Exception e){
     * e.printStackTrace();
     * EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI
     * .
     * RAISED,EventLevelI.FATAL,"ScheduleTopupActivationController[isImeiExist]"
     * ,"","","","Exception:"+e.getMessage());
     * throw new
     * BTSLBaseException(this,"isImeiExist",SelfTopUpErrorCodesI.ERROR_EXCEPTION
     * );
     * }
     * if(_log.isDebugEnabled()) {
     * _log.debug("isImeiExist","Exiting with status: "+exist);
     * }
     * return exist;
     * }
     */
}
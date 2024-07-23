package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;

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

public class ModifyCardDetailsController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ModifyCardDetailsController.class.getName());
    public static OperatorUtilI _operatorUtil = null;
    static {
        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, " DeleteBuddyController [initialize]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }
        String _msisdn = null;
        String _filteredMSISDN = null;
        String _imei = null;
        String _nickName = null;
        String _newNickName = null;
        boolean isAlreadyScheduled = false;
        // TYPE=<STPMCREQ><IMEI><Nick Name><New Nick Name><PIN>
        Connection con = null;
        try {
            con = OracleUtil.getConnection();
            CardDetailsDAO carddetailsDAO = new CardDetailsDAO();
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (senderVO == null) {
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.SUBSCRIBER_NOT_REGISTERED);
            }
            // Pin validation start
            String[] args = p_requestVO.getRequestMessageArray();// converted to
                                                                 // the array
            int messagelength = args.length;
            switch (messagelength) {
            case (PretupsI.MESSAGE_LENGTH_MODIFY_CARD - 1): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(senderVO.getPin())))) {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_CARD_DETAILS_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);
                }
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_MODIFY_CARD): {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(senderVO.getPin())))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[4]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                            con.commit();
                        }
                        throw be;
                    }
                }
                break;
            }
            }
            // Pin validation end
            _imei = args[1]; // check needs to be put
            _nickName = args[2].toUpperCase();
            _newNickName = args[3].toUpperCase();
            _msisdn = p_requestVO.getRequestMSISDN();
            // _newNickName=args[5];
            // MSISDN validation start (though already verified in the p2p
            // subscriber receiver servlet)
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
                // MSISDN validation end
            }

            // IMEI verification
            // boolean imeiExist=
            // isImeiExist(con,_msisdn,_imei,senderVO.getUserID());
            boolean imeiExist = _imei.trim().equals(senderVO.getImei()) ? true : false;
            if (!imeiExist) {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_IMEI);
            }
            // IMEI verification ends
            // Nick validation begins
            validateNickName(_nickName, PretupsI.NAME_ALLOWED_LENGTH, p_requestVO);
            // Nick validation ends
            // New Nick Validation Begins
            validateNickName(_newNickName, PretupsI.NAME_ALLOWED_LENGTH, p_requestVO);
            // New Nick Validation Ends
            // now we shall check whether the row exist for this MSISDN and IMEI
            // and said NICK
            boolean oldNickExist = isNickExist(con, _msisdn, _imei, _nickName, senderVO.getUserID());
            boolean newNickExist = false;
            if (oldNickExist) {
                newNickExist = isNickExist(con, _msisdn, _imei, _newNickName, senderVO.getUserID());
            } else {
                _log.error("process", "PROVIDED NICK NAME DOESN'T EXIST ");
                String msgArr1[] = { _nickName };
                p_requestVO.setMessageArguments(msgArr1);
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.INVALID_OLD_NICK);
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_OLD_NICK, msgArr1);
            }

            isAlreadyScheduled = subscriberDAO.checkAlreadyEnabledCard(con, senderVO.getUserID(), _nickName);
            if (!isAlreadyScheduled) {
                if (oldNickExist && !newNickExist) {
                    if (!_nickName.trim().equals(_newNickName.trim())) {
                        // then update the old nick with the new nick ; else
                        // exception
                        boolean isUpdateSuccess = carddetailsDAO.updateNick(con, _msisdn, _imei, _nickName, _newNickName, senderVO.getUserID());
                        if (isUpdateSuccess) {
                            // success Response
                            _log.debug("ModifyCardDetailsController process", "Successful ");
                            String msgArr[] = { _nickName, _newNickName };
                            p_requestVO.setMessageArguments(msgArr);
                            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.CARD_MODIFY_SUCCESS);
                        } else {
                            throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_INVALID_MSISDN);
                        }
                    } else {
                        throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.ERROR_NEW_NICK_SAME_AS_OLD);
                    }
                } else if (newNickExist) {
                    _log.error("process", "NEW NICK NAME ALREADY EXIST in CARD_DETAILS");
                    String msgArr[] = { _newNickName };
                    p_requestVO.setMessageArguments(msgArr);
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.INVALID_NEW_NICK);
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_NEW_NICK, msgArr);
                }
            } else {
                _log.error("process", "This card can not be modified. It is already associated with the schedule Top Up service.");
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.INVALID_NEW_NICK_ALREADY_SCHEDULED);
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.INVALID_NEW_NICK_ALREADY_SCHEDULED);
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[process]", "", "", "", "Getting Exception:" + e.getMessage());
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
                _log.debug("process", " Exited ");
            }
        }

    }

    public void validateNickName(String p_nickName, int p_allowedNameLength, RequestVO p_requestVO) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("validateNickName", "Entered p_nickName:" + p_nickName);
        }

        try {
            if (BTSLUtil.isNullString(p_nickName)) {
                throw new BTSLBaseException(this, "validateNickName", SelfTopUpErrorCodesI.ERROR_NICK_NAME_MANDATORY);
            }

            p_nickName = p_nickName.trim();

            if (p_nickName.length() > p_allowedNameLength) {
                String msgArr[] = { String.valueOf(PretupsI.NAME_ALLOWED_LENGTH) };
                p_requestVO.setMessageArguments(msgArr);
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.ERROR_NICK_NAME_EXCEED_LENGTH);
                throw new BTSLBaseException(this, "validateDetails", SelfTopUpErrorCodesI.ERROR_NICK_NAME_EXCEED_LENGTH, 0, msgArr, null);
            }
            if (!BTSLUtil.isAlphaNumeric(p_nickName)) {
                throw new BTSLBaseException(this, "validateNickName", SelfTopUpErrorCodesI.ERROR_NICK_NAME_SP_CHARACTERS);
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[validateNickName]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "validateNickName", SelfTopUpErrorCodesI.ERROR_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("validateNickName", "Exiting ");
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
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ModifyCardController[isNickExist]", "", "", "", "Exception:" + e.getMessage());
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
     * .RAISED,EventLevelI.FATAL,"ModifyCardController[isImeiExist]","","","",
     * "Exception:"+e.getMessage());
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

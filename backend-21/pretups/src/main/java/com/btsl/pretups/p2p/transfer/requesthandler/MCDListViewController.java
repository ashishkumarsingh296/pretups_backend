package com.btsl.pretups.p2p.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

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
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.p2p.transfer.businesslogic.MCDTxnDAO;

public class MCDListViewController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(MCDListViewController.class.getName());
    private String _requestIDStr;
    private static OperatorUtilI _operatorUtil = null;
    private String _senderMsisdn = null;
    private Locale _senderLocale = null;
    private SenderVO _senderVO;
    private String _listName = null;
    private String _gatewayCode = null;
    private ArrayList _buddyList = null;
    private String _finalMessage = null;
    // Loads operator specific class
    static {
        final String METHOD_NAME = "static";
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PrepaidControllerMultTransfer[initialize]", "", "",
                "", "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
		Connection con = null;
		MComConnectionI mcomCon = null;
        _requestIDStr = p_requestVO.getRequestIDStr();
        final MCDTxnDAO mcdtxnDAO = new MCDTxnDAO();
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestIDStr, "Entered");
        }
        try {
            _senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (_senderVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_NO_SUBSCRIBER);
            }
            _listName = p_requestVO.getMcdListName();
            _senderMsisdn = p_requestVO.getRequestMSISDN();
            _senderLocale = _senderVO.getLocale();
            _gatewayCode = p_requestVO.getRequestGatewayCode();
            try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
                validateSenderPin(con, p_requestVO);
                _buddyList = mcdtxnDAO.loadBuddySelectorAmountList(con, _senderVO.getUserID(), _listName);
                if (_buddyList == null || _buddyList.isEmpty()) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_VIEW_NO_RECORD);
                } else {
                    prepareFinalMessage(_buddyList);
                    if(BTSLUtil.isNullString(p_requestVO.getMcdListName())){
                    	
						p_requestVO.setMcdListName(_listName);
                	}
                }
            } catch (BTSLBaseException be) {
                _log.errorTrace(METHOD_NAME, be);
                _log.error("process", "BTSLBaseException be:" + be.getMessage());
                throw be;

            } catch (Exception e) {
                _log.error("process", "Exception e:" + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, METHOD_NAME, "Exception in processing request");

            }

        } catch (BTSLBaseException be) {

            _log.error("process", "BTSLBaseException be:" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setSuccessTxn(false);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
            }
        } catch (Exception e) {
            _log.error("process", "Exception e:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            if (p_requestVO.isSuccessTxn()) {
                final String key = p_requestVO.getMessageCode();
                if (_senderLocale == null) {
                    _senderLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                }
                if (!BTSLUtil.isNullString(_finalMessage)) {
                    p_requestVO.setSenderReturnMessage(_finalMessage);
                    (new PushMessage(_senderMsisdn, getSenderSuccessMessage(_senderLocale, _listName), "", _gatewayCode, _senderLocale)).push();
                } else if (key != null)

                {

                    (new PushMessage(_senderMsisdn, getSenderMessage(_senderLocale, _listName, key), "", _gatewayCode, _senderLocale)).push();
                } else {
                    (new PushMessage(_senderMsisdn, getSenderFailMessage(_senderLocale, _listName), "", _gatewayCode, _senderLocale)).push();
                }
            }
            if (con != null) {

                try {
                	mcomCon.finalCommit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
				if (mcomCon != null) {
					mcomCon.close("MCDListViewController#process");
					mcomCon = null;
				}
                con = null;
            }
        }

    }

    public void prepareFinalMessage(ArrayList p_arrayList) {
        if (_log.isDebugEnabled()) {
            _log.debug("prepareFinalMessage", "Entered   p_arrayList " + p_arrayList);
        }
        MCDListVO mcdVO = null;
        final HashMap mcdMap = new HashMap();
        StringBuffer sbf = new StringBuffer();
        Iterator iterator;
       int arrayLists=p_arrayList.size();
        for (int i = 0; i < arrayLists; i++) {
            mcdVO = (MCDListVO) p_arrayList.get(i);
            if (mcdMap.get(mcdVO.getMsisdn()) != null) {
                mcdMap.put(mcdVO.getMsisdn(), mcdMap.get(mcdVO.getMsisdn()) + ":" + mcdVO.getSelector1() + ":" + mcdVO.getAmount1());
            } else {
                mcdMap.put(mcdVO.getMsisdn(), mcdVO.getMsisdn() + ":" + mcdVO.getSelector1() + ":" + mcdVO.getAmount1());
            }
            if(BTSLUtil.isNullString(_listName)) {
				_listName=mcdVO.getListName();
            }
        }
        final Set keySet = mcdMap.keySet();
        iterator = keySet.iterator();
        while (iterator.hasNext()) {

            sbf = sbf.append((String) mcdMap.get(iterator.next()) + ",");
        }
        _finalMessage = sbf.toString();
        if (_finalMessage != null) {
            _finalMessage = _finalMessage.substring(0, _finalMessage.length() - 1);
        }

    }

    private String getSenderSuccessMessage(Locale p_senderLocale, String p_listName) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderSuccessMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName);
        }
        String key = null;
        String[] messageArgArray = null;

        key = PretupsErrorCodesI.P2P_ERROR_MCD_LIST_VIEW_SUCCES;
        messageArgArray = new String[] { p_listName, _finalMessage };

        return BTSLUtil.getMessage(p_senderLocale, key, messageArgArray);
    }

    private String getSenderFailMessage(Locale p_senderLocale, String p_listName) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderFailMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName);
        }
        String key = null;
        String[] messageArgArray = null;

        key = PretupsErrorCodesI.P2P_ERROR_MCD_LIST_VIEW_FAIL;
        messageArgArray = new String[] { p_listName };

        return BTSLUtil.getMessage(p_senderLocale, key, messageArgArray);
    }

    private String getSenderMessage(Locale p_senderLocale, String p_listName, String key) {
        if (_log.isDebugEnabled()) {
            _log.debug("getSenderMessage", " p_senderLocale=" + p_senderLocale.getDisplayName() + " p_listName=" + p_listName);
        }

        String[] messageArgArray = null;
        messageArgArray = new String[] { p_listName };

        return BTSLUtil.getMessage(p_senderLocale, key, messageArgArray);
    }

    public void validateSenderPin(Connection p_con, RequestVO p_requestVO) throws BTSLBaseException {
        final String METHOD_NAME = "validateSenderPin";
        if (_log.isDebugEnabled()) {
            _log.debug("validateSenderPin ", "Entered p_requestVO" + p_requestVO);
        }
        final String pin = p_requestVO.getMcdPIn();
        final String actualPin = _senderVO.getPin();
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) {
            if (BTSLUtil.isNullString(pin)) {
                throw new BTSLBaseException("MultipleCreditListController", "validateSenderPin", PretupsErrorCodesI.P2P_MCDL_PIN_REQUIRED);
            }
            if (actualPin.equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                if (!BTSLUtil.isNullString(pin) && !pin.equals((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN))) {
                    BTSLUtil.validatePIN(pin);
                    _senderVO.setPin(BTSLUtil.encryptText(pin));
                    _senderVO.setPinUpdateReqd(true);
                    _senderVO.setActivateStatusReqd(true);
                }
            } else {
                try {
                    SubscriberBL.validatePIN(p_con, _senderVO, pin);
                } catch (BTSLBaseException be) {
                    _log.errorTrace(METHOD_NAME, be);
                    if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        try {
                            p_con.commit();
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
                            throw new BTSLBaseException("MultipleCreditListController", "validateSenderPin", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                        }
                    }
                    throw be;
                }
            }
        }

    }
}
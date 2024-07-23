package com.btsl.pretups.p2p.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDDAO;
import com.btsl.pretups.p2p.transfer.businesslogic.MCDListVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.util.BTSLUtil;
//import com.btsl.util.Constants;

public class MCDListCreditTransfer implements ServiceKeywordControllerI, Runnable {
    private Log _log = LogFactory.getLog(MCDListCreditTransfer.class.getName());
    private String _requestIDStr;
    private RequestVO _requestVO;
    private String _senderMsisdn = null;
    private SenderVO _senderVO;
    private String _listName = null;
    private String _selector = null;
    private ArrayList _buddyList = null;

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        _requestVO = p_requestVO;
        _requestIDStr = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug("MCDListCreditTransfer process", _requestIDStr, "ENTERED");
        }
        final MCDDAO mcdDAO = new MCDDAO();
        MCDListVO mcdListVO = null;
        Connection con = null;MComConnectionI mcomCon = null;

        String requestMessage = null;
        StringBuffer sbf = null;
        String decrMsg = null;
        String[] reqMsgArr = null;
        String seperator;
        try {
            _senderVO = (SenderVO) p_requestVO.getSenderVO();
            if (_senderVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_NO_SUBSCRIBER);
            }

            _listName = p_requestVO.getMcdListName();
            _senderMsisdn = p_requestVO.getRequestMSISDN();
            decrMsg = p_requestVO.getDecryptedMessage();

            seperator = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR));
            seperator = "\\" + seperator;
            reqMsgArr = decrMsg.split(seperator);

            _selector = reqMsgArr[2];

            try {

            	mcomCon = new MComConnection();con=mcomCon.getConnection();
                validateSenderPin(con, p_requestVO);
                _buddyList = mcdDAO.loadBuddyListDetails(con, _senderVO.getUserID(), _listName, _selector);
                if (_buddyList == null || _buddyList.isEmpty()) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_MCD_LIST_VIEW_NO_RECORD);
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
            sbf = new StringBuffer();
	
			/*String selectorMapping = _selector;            
            try{
            	selectorMapping = Constants.getProperty(_selector);            	
            }
            catch(Exception e){
            	_log.errorTrace(METHOD_NAME, e);
            }*/
            for (int i = 0; i < _buddyList.size(); i++) {
                mcdListVO = (MCDListVO) _buddyList.get(i);
                sbf = sbf.append(PretupsI.SERVICE_TYPE_P2PRECHARGE + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + mcdListVO.getMsisdn() + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + mcdListVO
                        .getAmount1() + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + mcdListVO.getSelector1() + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + "0" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + "1357" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_DIFFERENT_REQUEST_SEPERATOR)));
				//sbf = sbf.append(PretupsI.SERVICE_TYPE_P2PRECHARGE + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + mcdListVO.getMsisdn() + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) 
                //+ mcdListVO.getAmount1() + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + selectorMapping + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + "0" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + "1357" + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_PLAIN_SMS_SEPARATOR)) + ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.MCDL_DIFFERENT_REQUEST_SEPERATOR)));	
            }
            requestMessage = sbf.toString();
            _requestVO.setDecryptedMessage(requestMessage);
            //_requestVO.setReqSelector(selectorMapping);            
            final Thread _controllerThread = new Thread(this);
            _controllerThread.start();
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
			if (mcomCon != null) {
				mcomCon.close("MCDListCreditTransfer#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("PrepaidControllerMultTransfer process", "Exiting");
            }
        }
    }

    public void run() {
        final MCDListCreditTransferThread controllerThread = new MCDListCreditTransferThread();
        controllerThread.process(_requestVO);
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
            if (actualPin.equalsIgnoreCase(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
                // if(!pin.equals(PretupsI.DEFAULT_P2P_PIN))
                if (!BTSLUtil.isNullString(pin) && !pin.equals(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)))) {
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

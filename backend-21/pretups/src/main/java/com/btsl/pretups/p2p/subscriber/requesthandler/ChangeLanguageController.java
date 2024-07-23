/**
 * @(#)ChangeLanguageController.java
 *                                   This controller is for the p2p module to
 *                                   change the language/locale
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Sandeep Goel Oct 29, 2005 Initital Creation
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 */

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;

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
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.p2p.subscriber.businesslogic.SubscriberTxnDAO;

/**
 * 
 */
public class ChangeLanguageController implements ServiceKeywordControllerI {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private String _requestID = null;

    /**
     * 
     * @param p_requestVO
     *            RequestVO
     * @see com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(RequestVO)
     */
    public void process(RequestVO p_requestVO) {
        _requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled()) {
            _log.debug("process", _requestID, "Entered " + p_requestVO);
        }
        final String methodName = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            // <Key Word> <PIN> < LanguageCode>
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            final String[] args = p_requestVO.getRequestMessageArray();
            String languageCode = null;
            final String actualPin = senderVO.getPin();

            final int messageLength = args.length;
            if (messageLength > PretupsI.P2P_MESSAGE_LENGTH_CHANGE_LANGUAGE || messageLength == 1) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_INVALID_CHGELANG_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            switch (messageLength) {
            case 2: {
                // As discussed with Abhijit sir pin is not mandatory in change
                // language
                languageCode = args[1];
                break;

            }
            case 3: {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[2]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey()
                            .equals(PretupsErrorCodesI.ERROR_SNDR_PINBLOCK)))) {
                        	mcomCon.finalCommit();
                        }
                        throw be;
                    }
                }
                languageCode = args[1];
                break;
            }
            }
            if (!BTSLUtil.isNumeric(languageCode)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_LANGUAGECODE_NOTNUMERIC);
            }
            final SubscriberDAO subscriberDAO = new SubscriberDAO();
            final SubscriberTxnDAO subscribertxnDAO = new SubscriberTxnDAO();
            // Check the language Code
            final ChangeLocaleVO changeLocaleVO = subscribertxnDAO.loadLanguageDetails(con, languageCode);
            if (changeLocaleVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
            }
            final int updateCount = subscriberDAO
                .updateLanguageAndCountry(con, changeLocaleVO.getLanguageCode(), changeLocaleVO.getCountry(), p_requestVO.getFilteredMSISDN());
            if (updateCount > 0) {
            	mcomCon.finalCommit();
                final String arr[] = { changeLocaleVO.getLanguageName() };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_LANGUAGE_UPDATE_SUCCESS);
                p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(languageCode));
                return;
            } else {
            	mcomCon.finalRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChangeLanguageController[process]", "", "", "",
                    "Exception: Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_LANGUAGE_UPDATE_FAILED);
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
            _log.error("process", _requestID, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_LANGUAGE_UPDATE_FAILED);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeLanguageController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_LANGUAGE_UPDATE_FAILED);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChangeLanguageController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", _requestID, " Exited ");
            }
        }
    }
}

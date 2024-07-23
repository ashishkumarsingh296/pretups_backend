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
import com.selftopup.pretups.common.SelfTopUpErrorCodesI;
import com.selftopup.pretups.common.PretupsI;
import com.selftopup.pretups.master.businesslogic.LocaleMasterCache;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.selftopup.pretups.preference.businesslogic.SystemPreferences;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.util.BTSLUtil;
import com.selftopup.util.OracleUtil;

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
     * @see com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(RequestVO)
     */
    public void process(RequestVO p_requestVO) {
        _requestID = p_requestVO.getRequestIDStr();
        if (_log.isDebugEnabled())
            _log.debug("process", _requestID, "Entered " + p_requestVO);
        Connection con = null;
        try {
            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            // <Key Word> <PIN> < LanguageCode>
            con = OracleUtil.getConnection();
            String[] args = p_requestVO.getRequestMessageArray();
            String languageCode = null;
            String actualPin = senderVO.getPin();

            int messageLength = args.length;
            if (messageLength > PretupsI.P2P_MESSAGE_LENGTH_CHANGE_LANGUAGE || messageLength == 1)
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_CHGELANG_REPORTREQUESTFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() }, null);

            switch (messageLength) {
            case 2: {
                // As discussed with Abhijit sir pin is not mandatory in change
                // language
                // if((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))
                // {
                languageCode = args[1];
                break;
                // }
                // else
                // {
                // throw new
                // BTSLBaseException(this,"process",PretupsErrorCodesI.P2P_ERROR_INVALID_CHGELANG_REPORTREQUESTFORMAT,0,new
                // String[]{p_requestVO.getActualMessageFormat()},null);
                // }
            }
            case 3: {
                if ((SystemPreferences.CP2P_PIN_VALIDATION_REQUIRED) && !((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN).equals(BTSLUtil.decryptText(actualPin)))) {
                    try {
                        SubscriberBL.validatePIN(con, senderVO, args[2]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_INVALID_PIN)) || (be.getMessageKey().equals(SelfTopUpErrorCodesI.ERROR_SNDR_PINBLOCK))))
                            con.commit();
                        throw be;
                    }
                }
                languageCode = args[1];
                break;
            }
            }
            if (!BTSLUtil.isNumeric(languageCode)) {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_LANGUAGECODE_NOTNUMERIC);
            }
            SubscriberDAO subscriberDAO = new SubscriberDAO();
            // Check the language Code
            ChangeLocaleVO changeLocaleVO = subscriberDAO.loadLanguageDetails(con, languageCode);
            if (changeLocaleVO == null) {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_INVALID_LANGUAGECODE);
            }
            int updateCount = subscriberDAO.updateLanguageAndCountry(con, changeLocaleVO.getLanguageCode(), changeLocaleVO.getCountry(), p_requestVO.getFilteredMSISDN());
            if (updateCount > 0) {
                con.commit();
                String arr[] = { changeLocaleVO.getLanguageName() };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_LANGUAGE_UPDATE_SUCCESS);
                p_requestVO.setLocale(LocaleMasterCache.getLocaleFromCodeDetails(languageCode));
                return;
            } else {
                con.rollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChangeLanguageController[process]", "", "", "", "Exception: Update count <=0 ");
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_LANGUAGE_UPDATE_FAILED);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
            }
            _log.error("process", _requestID, "BTSLBaseException " + be.getMessage());
            be.printStackTrace();
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_LANGUAGE_UPDATE_FAILED);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeLanguageController[process]", "", "", "", "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_LANGUAGE_UPDATE_FAILED);
            return;
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("process", _requestID, " Exited ");
        }
    }
}

/**
 * @(#)ChangeLanguageController.java
 *                                   This controller is for the C2S module to
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
 *                                   Gurjeet Singh Bedi Dec 03,2005 Modified for
 *                                   PIN position changes
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 */

package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ChangeLocaleVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.p2p.subscriber.businesslogic.SubscriberTxnDAO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

/**
 * 
 */
public class ChangeLanguageController implements ServiceKeywordControllerI {
    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * 
     * @param p_requestVO
     *            RequestVO
     * @see com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI#process(RequestVO)
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            final ChannelUserVO channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
                // message Format
                // <Key Word> < LanguageCode> <PIN>
            }

            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (_log.isDebugEnabled()) {
                _log.debug("process", "messageLen" + messageArr.length);
            }
            String languageCode = null;
            final int messageLen = messageArr.length;
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            switch (messageLen) {
            case PretupsI.C2S_MESSAGE_LENGTH_CHANGE_LANGUAGE: {

                if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
                    try {
                        ChannelUserBL.validatePIN(con, channelUserVO, messageArr[2]);
                    } catch (BTSLBaseException be) {
                        if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
                                        .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
                           mcomCon.finalCommit();
                        }
                        throw be;
                    }
                    languageCode = messageArr[1];
                }
                break;
            }
            case PretupsI.C2S_MESSAGE_LENGTH_CHANGE_LANGUAGE - 1: {
                languageCode = messageArr[1];
                break;
                /*
                 * if((channelUserVO.getUserPhoneVO()).getPinRequired().equals(
                 * PretupsI.NO))
                 * {
                 * languageCode = messageArr[1];
                 * break;
                 * }
                 * else
                 * throw new
                 * BTSLBaseException(this,"process",PretupsErrorCodesI.
                 * CHNL_ERROR_CHLAN_INVALIDMESSAGEFORMAT,0,new
                 * String[]{p_requestVO.getActualMessageFormat()},null);
                 */
            }
            default:
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.CHNL_ERROR_CHLAN_INVALIDMESSAGEFORMAT, 0,
                                new String[] { p_requestVO.getActualMessageFormat() }, null);
            }
            if (!BTSLUtil.isNumeric(languageCode)) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_LANGUAGECODE_NOTNUMERIC);
            }
            final SubscriberTxnDAO subscribertxnDAO = new SubscriberTxnDAO();
            // Check the language Code
            final ChangeLocaleVO changeLocaleVO = subscribertxnDAO.loadLanguageDetails(con, languageCode);
            if (changeLocaleVO == null) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_ERROR_INVALID_LANGUAGECODE);
            }
            final int updateCount = channelUserTxnDAO.updateLanguageInfo(con, changeLocaleVO.getLanguageCode(), changeLocaleVO.getCountry(), userPhoneVO.getUserPhonesId());
            if (updateCount > 0) {
                mcomCon.finalCommit();
                channelUserVO.getUserPhoneVO().setPhoneLanguage(changeLocaleVO.getLanguageCode());
                channelUserVO.getUserPhoneVO().setCountry(changeLocaleVO.getCountry());
                p_requestVO.setLocale(new Locale(changeLocaleVO.getLanguageCode(), changeLocaleVO.getCountry()));
                final String arr[] = { changeLocaleVO.getLanguageName() };
                p_requestVO.setMessageArguments(arr);
                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_LANGUAGE_UPDATE_SUCCESS);
                return;
            } else {
                mcomCon.partialRollback();
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "ChangeLanguageController[process]", "", "", "",
                                "Exception:Update count <=0 ");
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.C2S_LANGUAGE_UPDATE_FAILED);
            }
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                   mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_LANGUAGE_UPDATE_FAILED);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeLanguageController[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_LANGUAGE_UPDATE_FAILED);
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChangeLanguageController#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
        return;
    }
}

/**
 * @(#)ResumeServicesController.java
 *                                   Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                   All Rights Reserved
 *                                   Controller class for resuming the P2P
 *                                   Services
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   Author Date History
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 *                                   avinash.kamthan June 22, 2005 Initital
 *                                   Creation
 *                                   Gurjeet Singh Bedi 26/06/06 Modified
 *                                   ------------------------------------------
 *                                   --
 *                                   ------------------------------------------
 *                                   -----------
 * 
 */

package com.btsl.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Date;

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
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.btsl.pretups.p2p.subscriber.businesslogic.SubscriberDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.util.BTSLUtil;

public class ResumeServicesController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(ResumeServicesController.class.getName());

    /**
     * Process the resume request of the user
     */
    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());
        }

        final String methodName = "process";
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            final SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            final String actualPin = senderVO.getPin();
            final String[] args = p_requestVO.getRequestMessageArray();
            final int messageLength = args.length;

            switch (messageLength) {
            case (PretupsI.MESSAGE_LENGTH_RESUME - 1): {
                if ((((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CP2P_PIN_VALIDATION_REQUIRED)).booleanValue()) && !(BTSLUtil.encryptText((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.P2P_DEFAULT_SMSPIN)).equals(actualPin))) {
                    throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_RESUME_SERVCE_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                        .getActualMessageFormat() }, null);
                }
                break;
            }
            case (PretupsI.MESSAGE_LENGTH_RESUME): {
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
                break;
            }
            default:
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.P2P_ERROR_RESUME_SERVCE_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO
                    .getActualMessageFormat() }, null);
            }

            // to check whether user is suspended before resuming himself
            if (!(PretupsI.USER_STATUS_SUSPEND.equals(senderVO.getStatus()))) {
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_STATUS_NOTSUSPENDED);
            }

            final Date currentDate = new Date();
            senderVO.setStatus(PretupsI.USER_STATUS_ACTIVE);
            senderVO.setModifiedOn(currentDate);
            senderVO.setModifiedBy(PretupsI.SYSTEM_USER);

            // call the DAO method to change the user status
            final SubscriberDAO subscriberDAO = new SubscriberDAO();
            final int count = subscriberDAO.updateSubscriberStatus(con, senderVO);
            if (count > 0) {
            	mcomCon.finalCommit();
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_STATUS_RESUME_SUCCESS);
            } else {
            	mcomCon.finalRollback();
                throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_STATUS_RESUME_FAILED);
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException while resuming services for =" + p_requestVO.getFilteredMSISDN() + " getting exception=" + be.getMessage());
            _log.errorTrace(methodName, be);
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.USER_STATUS_RESUME_FAILED);
            }
        } catch (Exception e) {
            try {
                if (con != null) {
                	mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(methodName, ee);
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception while resuming services for =" + p_requestVO.getFilteredMSISDN() + " getting exception=" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ResumeServicesController[process]", p_requestVO
                .getFilteredMSISDN(), "", "", "Exception while resuming services:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
        	if(mcomCon != null){mcomCon.close("ResumeServicesController#process");mcomCon=null;}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }
    }
}

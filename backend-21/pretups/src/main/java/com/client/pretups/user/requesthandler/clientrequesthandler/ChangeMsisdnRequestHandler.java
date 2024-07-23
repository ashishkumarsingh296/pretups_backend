/**
 * @(#)ChangePinRequestHandler.java
 *                                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 *                                  This class use for changing the MSISDN of
 *                                  primary user.
 *                                  <description>
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Pushkar Sharma Dec 15, 2014 Initital
 *                                  Creation
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 * 
 */

package com.client.pretups.user.requesthandler.clientrequesthandler;

import java.sql.Connection;

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
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class ChangeMsisdnRequestHandler implements ServiceKeywordControllerI {

    private Log log = LogFactory.getLog(ChangeMsisdnRequestHandler.class.getName());

    public void process(RequestVO requestVO) {

        final String methodName = "process";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered " + requestVO);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;

        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final ChannelUserVO userVO = (ChannelUserVO) requestVO.getSenderVO();
            if (!userVO.isStaffUser()) {
                channelUserVO = (ChannelUserVO) requestVO.getSenderVO();
            } else {
                channelUserVO = ((ChannelUserVO) requestVO.getSenderVO()).getStaffUserDetails();
            }
            /**
             * Note: checks 1.) message should be in the mentioned format
             * <KeyWord> <NEW MSISDN> <MSISDN> <PIN>
             * 2.) NEW MSISDN should not exist for any other primary user.
             * 3.) MSISDN that needs to be changed.
             * 4.) pin should be numeric
             * 5.) pin length should be same as defined in the system
             */
            final String[] messageArr = requestVO.getRequestMessageArray();
            final String newMsisdn = messageArr[1];
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

            final ChannelUserVO channelUserNewVO = channelUserDAO.loadChannelUserDetails(con, newMsisdn);
            if (channelUserNewVO == null) {
                final int count = channelUserTxnDAO.updateUserPhonePrimaryMsisdn(con, newMsisdn, channelUserVO);
                if (count > 0) {
                    mcomCon.finalCommit();
                    requestVO.setMessageCode(PretupsErrorCodesI.C2S_MSISDN_CHANGE_SUCCESS);
                    ChannelUserLog.log("MSISDN CHANGE", channelUserVO, channelUserVO, false, "OLD MSISDN=" + channelUserVO.getMsisdn() + " NEW MSISDN=" + newMsisdn);
                } else {
                    requestVO.setSuccessTxn(false);
                    requestVO.setMessageCode(PretupsErrorCodesI.C2S_MSISDN_CHANGE_UPDATE_FAILURE);
                }

            } else {
                requestVO.setSuccessTxn(false);
                requestVO.setMessageCode(PretupsErrorCodesI.C2S_MSISDN_CHANGE_ALREADY_EXIST_FAILURE);
            }

        } catch (Exception e) {
            log.error(methodName, "BTSLBaseException " + e.getMessage());
            log.errorTrace(methodName, e);
            requestVO.setSuccessTxn(false);
            requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.error(methodName, "BTSLBaseException " + ee.getMessage());
                log.errorTrace(methodName, ee);
            }
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeMsisdnRequestHandler[process]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
			if (mcomCon != null) {
				mcomCon.close("ChangeMsisdnRequestHandler#process");
				mcomCon = null;
			}
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Exited ");
        }

    }
}

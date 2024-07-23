/**
 * @(#)ChangeRechargeStatusRequestHandler.java
 *                                             Copyright(c) 2005, Bharti
 *                                             Telesoft Ltd.
 *                                             All Rights Reserved
 *                                             This class use for changing the
 *                                             state of MSISDN [Barred or
 *                                             Unbarred]
 *                                             on the basis of state received in
 *                                             XML string.
 *                                             <description>
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             Author Date History
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             Pushkar Sharma Dec 15, 2014
 *                                             Initital Creation
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             ------------------------------
 * 
 */

package com.btsl.pretups.user.requesthandler;

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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class ChangeRechargeStatusRequestHandler implements ServiceKeywordControllerI {

    private Log log = LogFactory.getLog(ChangeRechargeStatusRequestHandler.class.getName());

    public void process(RequestVO p_requestVO) {

        if (log.isDebugEnabled()) {
            log.debug("process", " Entered " + p_requestVO);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;

        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final String[] messageArr = p_requestVO.getRequestMessageArray();
            final String state = messageArr[1];

            if ("1".equalsIgnoreCase(state) || "0".equalsIgnoreCase(state)) {
                final int count = channelUserTxnDAO.changeETopUpRechargeStatus(con, state, p_requestVO);
                if (count > 0) {
                   mcomCon.finalCommit();
                    p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_MSISDN_RECHARGE_STATUS_SUCCESS);
                }
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_MSISDN_RECHARGE_OTHER_STATUS);
                p_requestVO.setSuccessTxn(false);
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            log.error("process", "BTSLBaseException " + e.getMessage());
            log.errorTrace("process", e);
            try {
                if (con != null) {
                    mcomCon.finalRollback();
                }
            } catch (Exception ee) {
                log.error("process", "BTSLBaseException " + ee.getMessage());
                log.errorTrace("process", ee);
            }
            log.error("process", "BTSLBaseException " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChangeRechargeStatusRequestHandler[process]", "", "",
                            "", "Exception:" + e.getMessage());
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("ChangeRechargeStatusRequestHandler#process");
        		mcomCon=null;
        		}
        }
        if (log.isDebugEnabled()) {
            log.debug("process", " Exited ");
        }
    }
}

/**
 * @(#)BarredSubscriberController.java
 *                                     Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                     All Rights Reserved
 *                                     Self barring of subscribers from PreTUPS
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     Author Date History
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 *                                     avinash.kamthan Mar 29, 2005 Initital
 *                                     Creation
 *                                     Gurjeet Singh Bedi 26/06/06 Modified
 *                                     ----------------------------------------
 *                                     --
 *                                     ----------------------------------------
 *                                     ---------------
 * 
 */

package com.selftopup.pretups.p2p.subscriber.requesthandler;

import java.sql.Connection;
import java.util.Date;

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
import com.selftopup.pretups.network.businesslogic.NetworkPrefixCache;
import com.selftopup.pretups.network.businesslogic.NetworkPrefixVO;
import com.selftopup.pretups.p2p.subscriber.businesslogic.SubscriberBL;
import com.selftopup.pretups.receiver.RequestVO;
import com.selftopup.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.selftopup.pretups.subscriber.businesslogic.BarredUserDAO;
import com.selftopup.pretups.subscriber.businesslogic.BarredUserVO;
import com.selftopup.pretups.subscriber.businesslogic.SenderVO;
import com.selftopup.pretups.util.PretupsBL;
import com.selftopup.util.OracleUtil;

public class BarredSubscriberController implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(BarredSubscriberController.class.getName());

    public void process(RequestVO p_requestVO) {
        if (_log.isDebugEnabled())
            _log.debug("process", " Entered Request ID" + p_requestVO.getRequestID() + " Msisdn=" + p_requestVO.getFilteredMSISDN());

        Connection con = null;
        try {

            SenderVO senderVO = (SenderVO) p_requestVO.getSenderVO();
            BarredUserVO barredUserVO = null;
            // Only to non registered subscriber service is applicable
            if (senderVO == null) {
                senderVO = new SenderVO();
                senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
                senderVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(senderVO.getMsisdn()));
                senderVO.setNetworkCode(((NetworkPrefixVO) NetworkPrefixCache.getObject(senderVO.getMsisdnPrefix())).getNetworkCode());
                senderVO.setModule(p_requestVO.getModule());
                senderVO.setCreatedBy(PretupsI.SYSTEM_USER);
                senderVO.setCreatedOn(new Date());
                senderVO.setModifiedBy(PretupsI.SYSTEM_USER);
                senderVO.setModifiedOn(new Date());
                // to prepare the barredUserVO from senderVO
                barredUserVO = SubscriberBL.prepareBarredUserVO(senderVO, PretupsI.BARRED_TYPE_SELF, PretupsI.BARRED_USER_TYPE_SENDER, PretupsI.BARRED_SUBSCRIBER_SELF_RSN, senderVO.getCreatedBy());

                con = OracleUtil.getConnection();
                BarredUserDAO barredUserDAO = new BarredUserDAO();

                int status = barredUserDAO.addBarredUser(con, barredUserVO);

                if (status > 0) {
                    con.commit();
                    p_requestVO.setMessageCode(SelfTopUpErrorCodesI.BARRED_SUBSCRIBER_SUCCESS);
                } else {
                    throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.BARRED_SUBSCRIBER_FAILED);
                }
            } else {
                throw new BTSLBaseException(this, "process", SelfTopUpErrorCodesI.P2P_ERROR_REGSIETERD_SUBS_BARRING);
            }
        } catch (BTSLBaseException be) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception e) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else
                p_requestVO.setMessageCode(SelfTopUpErrorCodesI.BARRED_SUBSCRIBER_FAILED);

        } catch (Exception e) {
            try {
                if (con != null)
                    con.rollback();
            } catch (Exception ee) {
            }
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "Exception while self barring of=" + p_requestVO.getFilteredMSISDN() + " getting Exception =" + e.getMessage());
            e.printStackTrace();
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BarredSubscriberController[process]", p_requestVO.getFilteredMSISDN(), "", "", "Exception while self barring:" + e.getMessage());
            p_requestVO.setMessageCode(SelfTopUpErrorCodesI.P2P_ERROR_EXCEPTION);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (Exception e) {
            }
            if (_log.isDebugEnabled())
                _log.debug("process", " Exited ");
        }
    }
}

package com.client.pretups.user.requesthandler.clientrequesthandler;

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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;

public class GetMyNumberHandler implements ServiceKeywordControllerI {
    private static final Log LOG = LogFactory.getLog(GetMyNumberHandler.class.getName());

    public void process(RequestVO p_requestVO) {
        final String methodName = "process";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, " Entered " + p_requestVO);
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        ChannelUserVO channelUserVO = null;
        ChannelUserTxnDAO channelUserTxnDAO = null;
        try {
            channelUserTxnDAO = new ChannelUserTxnDAO();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            final ChannelUserVO userVO = (ChannelUserVO) p_requestVO.getSenderVO();
            if (!userVO.isStaffUser()) {
                channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            } else {
                channelUserVO = ((ChannelUserVO) p_requestVO.getSenderVO()).getStaffUserDetails();
            }

            final String messageArr[] = p_requestVO.getRequestMessageArray();
            if (messageArr.length == PretupsI.MESSAGE_LENGTH_GET_MSISDN) {
                if (!BTSLUtil.isNullString(messageArr[1])) {
                    channelUserVO = channelUserTxnDAO.getMyNumber(con, messageArr[1]);

                    p_requestVO.setMsisdn(channelUserVO.getMsisdn());
                    // set the argument which will be send to user as SMS part
                    final String[] arr = { channelUserVO.getMsisdn() };
                    p_requestVO.setMessageArguments(arr);
                    p_requestVO.setMessageCode(PretupsErrorCodesI.GET_MYNUMBER_SUCCESS);
                    return;
                } else {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GET_MYNUMBER_FAILED);
                }
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_INVALIDMESSAGEFORMAT, 0, new String[] { p_requestVO.getActualMessageFormat() },
                    null);
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            LOG.error(methodName, "BTSLBaseException " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "GetMyNumberRequestHandler[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("GetMyNumberHandler#process");
        		mcomCon=null;
        		}
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, " Exited ");
            }
        }
    }
}

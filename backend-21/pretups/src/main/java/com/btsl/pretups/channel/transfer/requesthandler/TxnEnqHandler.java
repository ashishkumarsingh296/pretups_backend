package com.btsl.pretups.channel.transfer.requesthandler;

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
import com.btsl.pretups.channel.transfer.businesslogic.TxnEnqDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.util.OperatorUtilI;

/**
 * @(#)TxnEnqHandler.java
 *                        Copyright(c) 2011, Comviva Technologies Ltd.
 *                        All Rights Reserved
 *                        this class use for returning the txn details to the
 *                        wrt to an enquiry based on the TxnID, Ref No or date
 *                        range
 *                        <description>
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ankuj Arora June 23,2011 Initital Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 * 
 */
public class TxnEnqHandler implements ServiceKeywordControllerI {
    private static Log _log = LogFactory.getLog(TxnEnqHandler.class.getName());
    private static OperatorUtilI _operatorUtil = null;

    // Loads operator specific class
    static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            _log.errorTrace("static", e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SidDeletionCOntroller[initialize]", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", " Entered " + p_requestVO);
        }
        Connection con = null;MComConnectionI mcomCon = null;
        final TxnEnqDAO txnenqDAO = new TxnEnqDAO();
        try {
            final int action = _operatorUtil.getTxnEnqryMessageArray(p_requestVO.getRequestMessageArray(), p_requestVO);
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            if ((action == 1) || (action == 2) || (action == 3) || (action == 4) || (action == 7) || (action == 8)) {
                txnenqDAO.loadDetailsFrmTxnIDO2C(con, p_requestVO, action);
            } else if ((action == 5) || (action == 9) || (action == 6)) {
                txnenqDAO.loadDetailsFrmTxnIDC2S(con, p_requestVO, action);
            }

        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error("process", "BTSLBaseException " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ee) {
                _log.errorTrace(METHOD_NAME, ee);
            }
            _log.error("process", "BTSLBaseException " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SidDeletionController[process]", "", "", "",
                "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
			if (mcomCon != null) {
				mcomCon.close("TxnEnqHandler#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", " Exited ");
            }
        }

    }
}

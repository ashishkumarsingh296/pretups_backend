package com.btsl.pretups.p2p.transfer.requesthandler;

import java.sql.Connection;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.PretupsBL;

/*
 * SKeyCancelController.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Gurjeet Singh Bedi 22/06/2005 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2005 Bharti Telesoft Ltd.
 * Controller Class for cancelling the last S Key for a particular Mobile number
 */

public class SKeyCancelController implements ServiceKeywordControllerI {

    private Log _log = LogFactory.getLog(this.getClass().getName());
    private Date _currentDate = null;
    private TransferVO _transferVO = null;
    private SenderVO _senderVO;
    private Connection con = null;
    private MComConnectionI mcomCon = null;

    public SKeyCancelController() {
        _currentDate = new Date();
    }

    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered");
        }
        try {
            _senderVO = (SenderVO) p_requestVO.getSenderVO();
            _transferVO.setSenderVO(_senderVO);
            _transferVO.setModule(p_requestVO.getModule());
            _transferVO.setInstanceID(p_requestVO.getInstanceID());
            _transferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
            _transferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
            mcomCon = new MComConnection();con=mcomCon.getConnection();
            // TO DO validate message format
            // new
            // MessageFormater().handleTransferMessageFormat(con,p_requestVO,_transferVO);
            PretupsBL.cancelSKey(con, _transferVO);
            if (_transferVO.getTransferStatus().equalsIgnoreCase(PretupsI.SKEY_CANCEL_SUCCESS)) {
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    throw new BTSLBaseException("SKeyCancelController", "process", PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
                }
            }
            // Set the message code to be send
            p_requestVO.setMessageCode(_transferVO.getTransferStatus());
        } catch (BTSLBaseException be) {
            _log.errorTrace(METHOD_NAME, be);
            p_requestVO.setMessageCode(be.getMessage());
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("SKeyCancelController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }
}

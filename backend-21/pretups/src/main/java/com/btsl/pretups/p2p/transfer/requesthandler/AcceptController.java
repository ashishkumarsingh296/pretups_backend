package com.btsl.pretups.p2p.transfer.requesthandler;

/**
 * @(#)AcceptController.java
 *                           Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                           All Rights Reserved
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Author Date History
 *                           --------------------------------------------------
 *                           -----------------------------------------------
 *                           Abhijit Chauhan June 18,2005 Initial Creation
 *                           --------------------------------------------------
 *                           ----------------------------------------------
 */

import java.util.ArrayList;
import java.util.Date;

import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.subscriber.businesslogic.SenderVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;

public class AcceptController implements ServiceKeywordControllerI {

    private static final Log _log = LogFactory.getLog(AcceptController.class.getName());
    private TransferVO _transferVO = null;
    private TransferItemVO _senderTransferItemVO = null;
    private TransferItemVO _receiverTransferItemVO = null;
    private String _senderMSISDN;
    private String _receiverMSISDN;
    private SenderVO _senderVO;
    private ReceiverVO _receiverVO;
    private String _senderSubscriberType;
    private Date _currentDate = null;
    private ArrayList _itemList = null;
    private String _intModCommunicationTypeS;
    private String _intModIPS;
    private String _intModPortS;
    private String _intModClassNameS;
    private String _intModCommunicationTypeR;
    private String _intModIPR;
    private String _intModPortR;
    private String _intModClassNameR;
    private String _transferID;
   
    private MComConnectionI mcomCon = null;

    public AcceptController() {
        _transferVO = new TransferVO();
        _currentDate = new Date();
    }
@Override
    public void process(RequestVO p_requestVO) {
        final String methodName = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("process", "Entered");
        }
        try {
            final PrepaidController prepaidController = new PrepaidController();
            prepaidController.process(p_requestVO);
        } catch (Exception e) {
            _log.debug("process", "Exxception e:" + e.getMessage());
            _log.errorTrace(methodName, e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("AcceptController#process");
				mcomCon = null;
			}
            if (_log.isDebugEnabled()) {
                _log.debug("process", "Exiting");
            }
        }
    }
}

package com.btsl.pretups.p2p.transfer.requesthandler;

/**
 * @(#)PrepaidControllerMultTransfer.java
 *                                        Copyright(c) 2011, Comviva
 *                                        Technologies Ltd.
 *                                        All Rights Reserved
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Author Date History
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        ---------------------
 *                                        Vinay Kumar Singh 01-August-2011
 *                                        Initial Creation
 *                                        --------------------------------------
 *                                        --------------------------------------
 *                                        --------------------
 */
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;

public class PrepaidControllerMultTransfer implements ServiceKeywordControllerI, Runnable {
    private Log _log = LogFactory.getLog(PrepaidControllerMultTransfer.class.getName());
    private String _requestIDStr;
    private RequestVO _requestVO;

    /**
     * This is the main entry method for P2P dedicated account transaction
     * This method will handle the request for dedicated account credit
     * transfer.
     * It will call the PrepaidControllerMultTransferThread controller to
     * process the request in thread and return back
     * the control to P2PReceiver for intermediate response to the USSD.
     * handle the request.
     */
    public void process(RequestVO p_requestVO) {
        final String METHOD_NAME = "process";
        if (_log.isDebugEnabled()) {
            _log.debug("PrepaidControllerMultTransfer process", _requestIDStr, "ENTERED");
        }
        _requestVO = p_requestVO;

        try {
            final Thread _controllerThread = new Thread(this);
            _controllerThread.start();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug("PrepaidControllerMultTransfer process", "Exiting");
            }
        }
    }

    public void run() {
        final PrepaidControllerMultTransferThread controllerThread = new PrepaidControllerMultTransferThread();
        controllerThread.process(_requestVO);
    }
}

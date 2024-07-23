package com.btsl.pretups.logging;

/*
 * GiveMeBalanceRequestResponseLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Amit Singh 28/08/09 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 * Class for logging request and response for Give Me Balance
 */

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.receiver.RequestVO;

public class GiveMeBalanceRequestResponseLog {

    private static Log _log = LogFactory.getFactory().getInstance(GiveMeBalanceRequestResponseLog.class.getName());

    /**
     * ensures no instantiation
     */
    private GiveMeBalanceRequestResponseLog(){
    	
    }
    
    
    /**
     * @param p_requestVO
     *            RequestVO
     *            Log information for request.
     */

    public static void log(RequestVO p_requestVO) {
        StringBuffer str = new StringBuffer();
        if (p_requestVO != null) {
            str.append("[INITIATOR_MSISDN: " + p_requestVO.getRequestMap().get("MSISDN1") + "] ");
            str.append("[DONOR_MSISDN: " + p_requestVO.getRequestMap().get("MSISDN2") + "] ");
            str.append("[AMOUNT: " + p_requestVO.getRequestMap().get("AMOUNT") + "] ");
            _log.info(" ", str.toString());
        }
    }

    /**
     * @param p_requestVO
     *            RequestVO
     * @param p_message
     *            String
     * @param p_initiatorMsisdn
     *            String
     * @param p_donorMsisdn
     *            String
     * @param p_transferAmt
     *            String
     *            Log information of response generation.
     */
    public static void log(RequestVO p_requestVO, String p_message, String p_initiatorMsisdn, String p_donorMsisdn, String p_transferAmt) {
        StringBuffer str = new StringBuffer();

        if (p_requestVO != null) {
            str.append("[MESSAGE: " + p_message + "] ");
            str.append("[INITIATOR_MSISDN: " + p_initiatorMsisdn + "] ");
            str.append("[DONOR_MSISDN: " + p_donorMsisdn + "] ");
            str.append("[AMOUNT: " + p_transferAmt + "] ");
        }
        _log.info(" ", str.toString());
    }
}

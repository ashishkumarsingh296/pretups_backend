package com.selftopup.pretups.logging;

/*
 * @(#)InterfaceTransactionLog.java
 * Name Date History
 * ------------------------------------------------------------------------
 * Manisha Jain 04/02/08 Initial Creation
 * ------------------------------------------------------------------------
 * Copyright (c) 2007 Bharti Telesoft Ltd.
 * Class for logging all the Messages that are sent to interface transaction
 * table
 */

import com.selftopup.logging.LogFactory;
import com.selftopup.logging.Log;
import com.selftopup.pretups.inter.module.InterfaceModuleVO;

public class InterfaceTransactionLog {

    private static Log _log = LogFactory.getFactory().getInstance(InterfaceTransactionLog.class.getName());

    /**
     * Method log for interface transaction table.
     * 
     * @param p_interfaceModuleVO
     *            InterfaceModuleVO
     */
    public static void log(InterfaceModuleVO p_interfaceModuleVO) {
        StringBuffer message = new StringBuffer();
        message.append("[ITID: " + p_interfaceModuleVO.getTxnID() + "] ");
        message.append("[MNO: " + p_interfaceModuleVO.getMsisdn() + "] ");
        message.append("[UTYPE: " + p_interfaceModuleVO.getUserType() + "] ");
        message.append("[RAMT: " + p_interfaceModuleVO.getRequestValue() + "] ");
        message.append("[PAMT: " + p_interfaceModuleVO.getPreviousBalance() + "] ");
        message.append("[NAMT: " + p_interfaceModuleVO.getPostBalance() + "] ");
        message.append("[VAL: " + p_interfaceModuleVO.getValidity() + "] ");
        message.append("[ITYPE: " + p_interfaceModuleVO.getInterfaceType() + "] ");
        message.append("[IID: " + p_interfaceModuleVO.getInterfaceID() + "] ");
        message.append("[ISTAT: " + p_interfaceModuleVO.getInterfaceResonseCode() + "] ");
        message.append("[REFID: " + p_interfaceModuleVO.getReferenceID() + "] ");
        message.append("[CGRP: " + p_interfaceModuleVO.getCardGroup() + "] ");
        message.append("[SRVCL: " + p_interfaceModuleVO.getServiceClass() + "] ");
        message.append("[SERTYPE: " + p_interfaceModuleVO.getServiceType() + "] ");
        message.append("[PEXP: " + p_interfaceModuleVO.getMsisdnPreviousExpiry() + "] ");
        message.append("[NEXP: " + p_interfaceModuleVO.getMsisdnNewExpiry() + "] ");
        message.append("[TXNSTAT: " + p_interfaceModuleVO.getTxnStatus() + "] ");
        message.append("[TRES: " + p_interfaceModuleVO.getTxnResponseReceived() + "] ");
        message.append("[TXNTYPE: " + p_interfaceModuleVO.getTxnType() + "] ");
        message.append("[TTIME: " + p_interfaceModuleVO.getTxnDateTime() + "] ");
        message.append("[STRT: " + p_interfaceModuleVO.getTxnStartTime() + "] ");
        message.append("[ENDT: " + p_interfaceModuleVO.getTxnEndTime() + "] ");
        message.append("[URLID: " + p_interfaceModuleVO.getUrlID() + "] ");
        message.append("[BNUS: " + p_interfaceModuleVO.getBonusValue() + "] ");
        message.append("[BVAL : " + p_interfaceModuleVO.getBonusValidity() + "] ");
        message.append("[BSMS : " + p_interfaceModuleVO.getBonusSMS() + "] ");
        message.append("[BMMS : " + p_interfaceModuleVO.getBonusMMS() + "] ");
        // added by vikask for updation of cardgroup
        message.append("[BMMSV : " + p_interfaceModuleVO.getBonusMMSValidity() + "] ");
        message.append("[BSMSV : " + p_interfaceModuleVO.getBonusSMSValidity() + "] ");
        message.append("[BCBV : " + p_interfaceModuleVO.getCreditbonusValidity() + "] ");
        // added by amit
        message.append("[IMPLICIT : " + p_interfaceModuleVO.getOnLine() + "] ");
        message.append("[COMBINED : " + p_interfaceModuleVO.getBoth() + "] ");
        _log.info(" ", message.toString());
    }
}

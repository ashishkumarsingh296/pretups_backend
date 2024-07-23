/**
 * @(#)BulkPushThread.java
 *                         Copyright(c) 2003, Bharti Telesoft Ltd.
 *                         All Rights Reserved
 *                         This class for creating Threads that call OtaMessage
 *                         for sending SMS
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 *                         Author Date History
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 * 
 *                         Gaurav Garg 31/12/2003 Initial Creation
 *                         ----------------------------------------------------
 *                         ---------------------------------------------
 */
package com.btsl.ota.bulkpush.businesslogic;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.ota.services.businesslogic.ServicesVO;
import com.btsl.ota.services.businesslogic.SimProfileVO;
import com.btsl.ota.util.OtaMessage;

public class BulkPushThread extends Thread {
    private static final Log logger = LogFactory.getLog(BulkPushThread.class.getName());
    private ServicesVO sVO;
    private SimProfileVO simProfileVO;
    private String tName;

    /**
     * Constructor Declaration
     * 
     * @param p_sVO
     *            ServicesVO (contains all the necessary info that is send to
     *            MSISDN like key,byte,transId etc)
     * @param threadName
     *            String (Name of thread)
     * @param p_simProfileVO
     *            SimProfileVO (This for validation)
     * @return String
     */
    public BulkPushThread(ServicesVO p_sVO, String threadName, SimProfileVO p_simProfileVO) {
        super(threadName);
        sVO = p_sVO;
        simProfileVO = p_simProfileVO;
        tName = threadName;
    }

    public BulkPushThread() {
        super();
    }

    public void run() {
        final String METHOD_NAME = "run";
        OtaMessage otaMessage = new OtaMessage();
        try {
            boolean flag = otaMessage.otaMessageSenderBulkPush(simProfileVO, sVO);
            if (flag) {
                logger.info("", "Success ::  Msisdn " + sVO.getMsisdn() + "Transaction Id" + sVO.getTransactionId() + " Thread Name :" + tName);
            } else {
                logger.error("", "Failure::  Msisdn " + sVO.getMsisdn() + "Transaction Id" + sVO.getTransactionId() + " Byte Code " + sVO.getByteCode() + " Thread Name :" + tName);
            }
        } catch (Exception e) {
            logger.errorTrace(METHOD_NAME, e);
            logger.error("", "Exception::  Msisdn " + sVO.getMsisdn() + "Transaction Id" + sVO.getTransactionId() + " Byte Code " + sVO.getByteCode() + " Thread Name :" + tName);
        }
    }
}

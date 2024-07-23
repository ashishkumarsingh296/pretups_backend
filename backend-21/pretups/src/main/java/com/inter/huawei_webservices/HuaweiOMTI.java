package com.inter.huawei_webservices;

/**
 * @HuaweiOMTI.java
 *                  Copyright(c) 2011, Comviva Technologies Pvt. Ltd.
 *                  All Rights Reserved
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Shashank Shukla March 11, 2011 Initial Creation
 *                  ------------------------------------------------------------
 *                  -----------------------------------
 *                  This interface defined some constant corresponding to the
 *                  HUAWEI OMT interface.
 */

public interface HuaweiOMTI {
    public int ACTION_ACCOUNT_INFO = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_CREDIT_TRANSFER = 3;
    public int ACTION_CREDIT_ADJUST = 4;
    public int ACTION_DEBIT_ADJUST = 5;
    public String RESULT_OK = "0";
    public String OPERATION_SUCCESSFULLY = "405000000";
    public String SUBSCRIBER_NOT_FOUND = "405610009";
    public String MAX_TXN_REACH = "1";
    public String C2S_REC_STATUS_NOT_ALLOWED = "405610071"; // C2S--This service
                                                            // can not be
                                                            // processed when
                                                            // the {***}
                                                            // subscriber is in
                                                            // the {***} state.
    public String P2P_STATUS_NOT_ALLOWED = "405914998"; // P2P
                                                        // Sender---Subscriber
                                                        // state error for
                                                        // sender/receiver.
}

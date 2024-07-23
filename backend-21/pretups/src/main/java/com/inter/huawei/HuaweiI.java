package com.inter.huawei;

/**
 * @HuaweiI.java
 *               Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *               All Rights Reserved
 *               --------------------------------------------------------------
 *               -----------------------------------
 *               Author Date History
 *               --------------------------------------------------------------
 *               -----------------------------------
 *               Ashish K Jan 24, 2007 Initial Creation
 *               --------------------------------------------------------------
 *               ----------------------------------
 *               This interface defined some constant corresponding to the
 *               HUAWEI interface.
 */
public interface HuaweiI {
    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_HEART_BEAT = 3;
    public int ACTION_LOGIN = 11;
    public int ACTION_LOGOUT = 12;
    public int ACTION_TXN_CANCEL = 5;
    public String RESULT_OK = "0";
    public String SUBSCRIBER_NOT_FOUND = "1001";
    public String MAX_TXN_REACH = "1";
    public int ACTION_CHECK_AMB_STATUS = 13;
}

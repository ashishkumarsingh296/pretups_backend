package com.inter.huaweiknfix;

/**
 * @HuaweiKnFixI.java
 *                    Copyright(c) 2009, Bharti Telesoft Int. Public Ltd.
 *                    All Rights Reserved
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Abhay January 26, 2009 Initial Creation
 *                    ----------------------------------------------------------
 *                    -------------------------------------
 *                    This interface defined some constant corresponding to the
 *                    HUAWEI interface.
 */
public interface HuaweiKnFixI {
    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_HEART_BEAT = 3;
    public int ACTION_VALIDITY_ADJUST = 4;
    public int ACTION_LOGIN = 11;
    public int ACTION_LOGOUT = 12;
    public int ACTION_TXN_CANCEL = 5;
    public String RESULT_OK = "0";

    // added by abhay for orange kenya
    public String SUBSCRIBER_NOT_FOUND = "1000";
    public String SUBSCRIBER_AC_NOT_ACTIVE = "1002";
    public String SUBSCRIBER_BARRED = "1003";
    public String SUBSCRIBER_BAL_INSUFFIC = "1004";
    public String SUBSCRIBER_AC_EXPIRED = "1006";
    public String SUBSCRIBER_IVR_NOT_SET = "1008";

    public String MAX_TXN_REACH = "1";

}

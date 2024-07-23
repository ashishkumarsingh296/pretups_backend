package com.inter.blin.cs5banglalink;

public interface CS5BanglalinkI {
    public int ACTION_ACCOUNT_INFO = 90;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_TRANSFER_SENDER_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_ACCOUNT_DETAILS = 91;
    public String HTTP_STATUS_200 = "200";
    public String RESULT_OK = "0,1,2";
    public String SUBSCRIBER_NOT_FOUND = "102";
    public String OLD_TRANSACTION_ID = "162";
    public String LDCC_SERVICE_OFFERING_ID = "15";
    public String LDCC_SERVICE_OFFERING_ACT_FLAG = "1";
    public int ACTION_DEDICATED_ACCOUNT_CD = 3;
    public int ACTION_TRANSFER_RECEIVER_CREDIT = 4;
    public int ACTION_TRANSFER_SENDER_CREDIT_BACK = 5;
}

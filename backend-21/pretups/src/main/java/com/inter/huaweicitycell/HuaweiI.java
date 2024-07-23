package com.inter.huaweicitycell;

public interface HuaweiI {

    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_HEART_BEAT = 3;
    public int ACTION_VALIDITY_ADJUST = 4;
    public int ACTION_TXN_CANCEL = 5;
    public String RECHARGE_RES_TYPE = "RR";
    public String ACCOUNT_INFO_RES_TYPE = "AR";
    public String DEBIT_RES_TYPE = "DR";

    public String RECHARGE_SUCCEEDED = "0000";
    public String HEARTBEAT_SUCCEEDED = "0001";
    public String HEARTBEAT_FAILED = "0002";
    public String RECHARGE_FAILED = "0003";
    public String ACCOUNT_INFO_FAILED = "0004";
    public String INVALID_RESPONSE = "0005";
    public String ACCOUNT_NUMBER_NOT_FOUND = "1001";
    public String ACCOUNT_EXPIRED = "1002";

    public String USED_FIRST_TIME_INVALID = "1003";
    public String SUBSCRIBER_NOT_ACTIVATED = "1004";

    public String INCORRECT_PIN = "1005";
    public String EXCEED_MAX_RECH_AMOUNT = "1006";

    public String NO_PPS_INFO = "1007";
    public String INSIFFICENT_ACC_BALANCE = "1008";
    public String RECH_FAILED = "1009";
    public String SYSTEM_EXCEPTION = "1010";
    public String TXN_SN_REPEATED = "1011";
    public String RECH_SUCCESS_LOGGING_FAILED = "1012";
    public String QUERY_AREA_CODE_FAILED = "1013";
    public String QUERY_VALI_PERD_RECH_AMT_FAILED = "1014";
    public String RECHING_MAX_NO_REG_CUST = "1015";
    public String SERVICE_DATA_NOT_CONFIGURE = "1018";
    public int RESPONSE_TYPE_VALIDATE = 37;
    public int RESPONSE_TYPE_RECHARGE = 33;

    public String MAX_TXN_REACH = "1";

}

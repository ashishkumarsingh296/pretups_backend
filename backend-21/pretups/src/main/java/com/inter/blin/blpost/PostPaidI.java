package com.inter.blin.blpost;

/**
 * @PostPaidI.java
 *                 Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                 All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Sanjeew K 30 Aug, 2016 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 This interface defined some constant corresponding to the
 *                 POST PAID interface.
 */
public interface PostPaidI {
    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_RECHARGE_CREDIT = 2;
    public String ONLINE_DB_SP = "1";
    public String QUEUE_TABLE = "2";
    public String CDR_FILE_FTP = "3";
    public String C2S_MODULE = "C2S";
    public String SP_SUCCESS_OK = "0";
    public String SUBSCRIBER_NOT_FOUND = "10";// Confirm for this error code
    public String NOT_POSTPAID_NO = "-1";// Number is not a valid Postpaid
    // number
}

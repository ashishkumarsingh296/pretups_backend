package com.inter.zteocm;

/**
 * @(#)ZTEOBOI.java
 *                  Copyright(c) 2011, Comviva Technologies Ltd.
 *                  All Rights Reserved
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Shashank Shukla Jun 20, 2012 Initial Creation
 * 
 *                  ------------------------------------------------------------
 *                  ------------------------------------
 */
public interface ZTEOCMI {
    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_TRANSFER_CREDIT = 3;
    public int ACTION_VALIDITY_ADJUST = 4;
    public int ACTION_TXN_CANCEL = 5;
    public String RESULT_OK = "0";
    public int RESULT_INT_OK = 0;
    public String MSISDN_NOT_FOUND = "-PRF-00025";
}
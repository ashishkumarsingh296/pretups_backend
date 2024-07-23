package com.inter.nokia;

/**
 * @(#)NokiaI
 *            Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *            All Rights Reserved
 *            ------------------------------------------------------------------
 *            -------------------------------
 *            Author Date History
 *            ------------------------------------------------------------------
 *            -------------------------------
 *            Ashish Kumar Jan 8, 2007 Initial Creation
 *            ------------------------------------------------------------------
 *            ------------------------------
 *            This interface defined some constant corresponding to the Nokia
 *            interface.
 */
public interface NokiaI {
    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_TXN_CANCEL = 5;
    public String RESULT_OK = "0";
    public String SUBSCRIBER_NOT_FOUND = "100";

}

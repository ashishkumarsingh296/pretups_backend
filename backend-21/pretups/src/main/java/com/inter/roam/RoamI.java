package com.inter.roam;

/**
 * @(#)RoamI.java
 *                Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                All Rights Reserved
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Author Date History
 *                --------------------------------------------------------------
 *                -----------------------------------
 *                Ashish Kumar Sep 19, 2006 Initial Creation
 *                --------------------------------------------------------------
 *                ----------------------------------
 *                Interface class for the Roam Interface
 */
public interface RoamI {
    // Action values that differentiate the type of request

    public int ACTION_ACCOUNT_INFO = 1;
    public int ACTION_CREDIT = 2;
    public int ACTION_DEBIT = 3;
    public int ACTION_TXN_CANCEL = 5;
    public String RESULT_OK = "200";
    public String SUBSCRIBER_NOT_FOUND = "102";
    public String HTTP_STATUS_200 = "200";
    public String MANDATORY_FILED_MISSING = "1001";// Used in Simulator
    public String UNKNOWN_ERROR = "1004";// Used in Simulator
    public String TRANSACTION_AMBIGUOUS = "250";

}

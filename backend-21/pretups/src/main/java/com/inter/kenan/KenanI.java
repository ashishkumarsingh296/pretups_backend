package com.inter.kenan;

/**
 * @(#)KenanI.java
 *                 Copyright(c) 2006, Bharti Telesoft Int. Public Ltd.
 *                 All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Ashish Kumar Nov 22, 2006 Initial Creation
 *                 ------------------------------------------------------------
 *                 ------------------------------------
 *                 Interface class for the Kenan Interface
 */
public interface KenanI {
    public int ACTION_ACCOUNT_INFO = 1;
    public int ACTION_CREDIT = 2;
    public int ACTION_DEBIT = 3;
    public int ACTION_TXN_CANCEL = 5;
    // Status that will from the response.
    public String RESULT_OK = "17300";// Used to represent the SUCCESS.
    public String SUBSCRIBER_NOT_FOUND = "17200";// Used to represent the FAIL.
    public String DATA_ERROR = "17400"; // Used in the simulator;
    public String PARSING_ERROR = "17500"; // Used in the simulator;
    public String HTTP_STATUS_200 = "200";// Used to represent the success
                                          // status of HTTP
}

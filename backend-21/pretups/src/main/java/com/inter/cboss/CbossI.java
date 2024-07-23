package com.inter.cboss;

/**
 * @(#)CbossI.java
 *                 Copyright(c) 2007, Bharti Telesoft Ltd.
 *                 All Rights Reserved
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Author Date History
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 Vinay Kumar Singh 29 July, 2007 Initial Creation
 *                 ------------------------------------------------------------
 *                 -------------------------------------
 *                 This interface defined some constant corresponding to the
 *                 CbossI interface and it will be used in CbossINHandler class.
 */
public interface CbossI {
    public int ACTION_ACCOUNT_INFO = 0; // Account info of suscriber.
    public int ACTION_IMMEDIATE_DEBIT = 1; // Action code for immediate debit
                                           // request
    public int ACTION_RECHARGE_CREDIT = 2; // Action code for credit recharge
                                           // request
    public int ACTION_SUBSCRIBER_TYPE = 3; // Type of the subscriber in the
                                           // system
    public String PRE = "PRE"; // Pre for Cboss
    public String POST = "POST"; // Post for the mobi
    public String UNKNOWN = "UNKNOWN"; // Not defined in PRE or POST
    public String RESULT_OK = "0";
    public String SUBTYPE_SUBSCRIBER_NOT_FOUND = "1"; // Subtype of the
                                                      // subscriber in the
                                                      // system
    public String INIT_ERROR = "1";
    public String SERVICE_ERROR = "2";
    public String WRONG_AMOUNT = "3";

}
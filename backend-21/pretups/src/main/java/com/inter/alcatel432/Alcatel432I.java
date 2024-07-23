package com.inter.alcatel432;

/**
 * @(#)AlcatelIN432Handler.java
 *                              Copyright(c) 2006, Bharti Telesoft Int. Public
 *                              Ltd.
 *                              All Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ashish Kumar May 03,2006 Initial Creation
 *                              ------------------------------------------------
 *                              ------------------------------------------------
 *                              Interface class for the Alcatel432 Interface
 */
public interface Alcatel432I {
    // Action values that differentiate the type of request
    public int ACTION_ACCOUNT_INFO = 90;
    public int ACTION_IMMEDIATE_DEBIT = 0;
    public int ACTION_RECHARGE_CREDIT = 1;
    public int ACTION_TWO_STEP_DEBIT_BOOK = 2;
    public int ACTION_TWO_STEP_DEBIT_UPDATE = 4;
    public int ACTION_TXN_CANCEL = 5;

    // Status that will get as the value of result element and status.

    public String RESULT_OK = "0";
    public String RESULT_ERROR_REJ_DUE_TO_SERVICE = "2"; // Failure due to a
                                                         // service problem
    public String RESULT_ERROR_MALFORMED_REQUEST = "3"; // Request structure is
                                                        // not valid
    public String RESULT_ERROR_MALFORMED_PARAM = "4"; // A parameter does not
                                                      // respect the format
                                                      // restrictions
    public String RESULT_ERROR_INVALID_PARAM = "5"; // A parameter has not been
                                                    // found in the database

    // Failure due to an inconsistent parameter, this value is used if the
    // debit/credit service detects that a fix
    // parameter such as 'cp_parameter_id' has changed compared with previous
    // request.
    public String RESULT_ERROR_UNEXP_VALUE = "6";

    public String RESULT_ERROR_TIME_OUT = "7"; // Transaction has already been
                                               // closed.
    public String RESULT_ERROR_MALFORMED_XML_PROLOG = "8"; // Received xml
                                                           // prolog does not
                                                           // equal waited
                                                           // prolog
    public String RESULT_ERROR_XML_PARSE = "9"; // Error during parsing
    public String RESULT_ERROR_NOT_ENOUGH_CREDIT = "10"; // Specific to debit
                                                         // that shows not
                                                         // enough credit on the
                                                         // account.
    public String RESULT_ERROR_ACC_NOT_FOUND = "201"; // Account not found or
                                                      // account profile not
                                                      // found.

    public String HTTP_STATUS_200 = "200";
    // Confirm the range like 202-217
    // public String RESULT_ERROR_NOK_ACC_STATUS ="202";

    // The debit amount is higher than the max. amount authorized per
    // transaction for a pure prepaid user.
    // This parameter defined pure prepaid account profile.
    public String RESULT_ERROR_HIGH_DEBIT = "243";

    // Confirm
    public String RESULT_ERROR_NO_MORE_AVAILABLE_CREDIT = "253";

    // public String RESULT_ERROR_TECH
}

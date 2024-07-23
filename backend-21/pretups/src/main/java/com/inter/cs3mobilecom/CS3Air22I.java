package com.inter.cs3mobilecom;

/**
 * @(#)CS3Air22I.java
 *                    Copyright(c) 2011, COMVIVA TECHNOLOGIES LIMITED. All
 *                    rights reserved.
 *                    COMVIVA PROPRIETARY/CONFIDENTIAL. Use is subject to
 *                    license terms.
 *                    All Rights Reserved
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Vinay Kumar Singh January 25, 2011 Initial Creation
 *                    ----------------------------------------------------------
 *                    --------------------------------------
 *                    Interface class for the CS3 Air2.2 Interface
 */
public interface CS3Air22I {
    // Action values that differentiate the type of request

    public int ACTION_ACCOUNT_INFO = 1;
    public int ACTION_CREDIT = 2;
    public int ACTION_DEBIT = 3;
    public int ACTION_TXN_CANCEL = 5;
    public int ACTION_SUPER_REFILLT_CREDIT = 6;// added for SuperRefillT
    // Status received from the response.
    public String RESULT_OK = "0";
    public String SUBSCRIBER_NOT_FOUND = "102";
    public String HTTP_STATUS_200 = "200";
    public String MANDATORY_PARAMETER_MISSING = "1001";// Used in Simulator
    public String UNKNOWN_ERROR = "1004";// Used in Simulator
}

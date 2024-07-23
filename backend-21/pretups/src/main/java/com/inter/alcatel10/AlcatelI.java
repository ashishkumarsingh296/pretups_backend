package com.inter.alcatel10;

/**
 * @(#)AlcatelI.java
 *                   Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
 *                   All Rights Reserved
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Author Date History
 *                   ----------------------------------------------------------
 *                   ---------------------------------------
 *                   Gurjeet Singh Bedi Oct 17,2005 Initial Creation
 *                   ----------------------------------------------------------
 *                   --------------------------------------
 *                   Interface class for the Alcatel Interface
 */

public interface AlcatelI {
    public int ACTION_ACCOUNT_INFO = 90;
    public int ACTION_IMMEDIATE_DEBIT = 0;
    public int ACTION_RECHARGE_CREDIT = 1;
    public int ACTION_TWO_STEP_DEBIT_BOOK = 2;
    public int ACTION_TWO_STEP_DEBIT_UPDATE = 4;
    public int ACTION_TXN_CANCEL = 5;

    public String RESULT_OK = "0";
    public String HTTP_STATUS_200 = "200";
    public String RESULT_ERROR_MALFORMED_REQUEST = "3";
    public String RESULT_ERROR_XML_PARSE = "9";
    public String RESULT_ERROR_201 = "201";
}

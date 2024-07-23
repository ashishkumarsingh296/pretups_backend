package com.inter.alcatel442;

/**
 * @(#)Alcatel442I.java
 *                      Copyright(c) 2007, Bharti Telesoft Int. Public Ltd.
 *                      All Rights Reserved
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Dhiraj Tiwari Oct 16,2007 Initial Creation
 *                      --------------------------------------------------------
 *                      ----------------------------------------
 *                      Interface class for the Alcatel442 Interface
 */

public interface Alcatel442I {
    public int ACTION_ACCOUNT_INFO = 90;
    public int ACTION_IMMEDIATE_DEBIT = 0;
    public int ACTION_RECHARGE_CREDIT = 1;
    public int ACTION_TXN_CANCEL = 5;
    public String RESULT_OK = "0";
    public String HTTP_STATUS_OK = "200";
    public String RESULT_ERROR_MALFORMED_REQUEST = "3";
    public String RESULT_ERROR_XML_PARSE = "9";
    public String RESULT_ERROR_ACC_NOT_FOUND = "201";
}

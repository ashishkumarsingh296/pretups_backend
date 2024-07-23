/*
 * Created on Jul 24, 2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.mobi;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface MobiI {

    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_SUBSCRIBER_TYPE = 4;
    public int ACTION_SINGLE_STEP_DEBIT_CREDIT = 3;
    public String POST = "POST";
    public String PRE = "PRE";
    public String UNKNOWN = "UNKNOWN";

    // ERROR CODES
    public String RESULT_OK = "0";
    public String SUBTYPE_SUBSCRIBER_NOT_FOUND = "1";
    public String SUBSCRIBER_NOT_FOUND = "1";
    public String ACNTINFO_PREPAID = "2";
    public String BLOCKED_POSTPAID = "3";
    public String TXN_NOT_POSSIBLE = "3";
    public String INVALID_AMOUNT = "4";
}

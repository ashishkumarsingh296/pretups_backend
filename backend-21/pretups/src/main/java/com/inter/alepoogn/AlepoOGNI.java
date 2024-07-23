/*
 * Created on Aug 08, 2011
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.alepoogn;

/**
 * @author shashank.shukla
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface AlepoOGNI {

    public int ACTION_ACCOUNT_INFO = 0;
    public int ACTION_IMMEDIATE_DEBIT = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_TRANSFER_CREDIT = 3;
    public int ACTION_VALIDITY_ADJUST = 4;
    public int ACTION_TXN_CANCEL = 5;
    public String RESULT_OK = "SOAP_SUCCESS";
    public String RESULT_SUCCESS = "0";
    public String RESULT_FAILURE = "1";
    public String MSISDN_NOT_FOUND = "NOT_EXIST";
    // public String DB_ERROR="DB_ERROR";
    // public String OTHER_ERROR="NOT_ALLOW";
}

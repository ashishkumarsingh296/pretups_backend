/*
 * Created on May 1, 2008
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.cs3vfe;

/**
 * @author dhiraj.tiwari
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public interface CS3VfeI {
    public int ACTION_GET_BAL_DATE = 90;
    public int ACTION_GET_ACCOUNT_DETAILS = 91;
    public int ACTION_DEBIT_ADJUST = 1;
    public int ACTION_RECHARGE_CREDIT = 2;
    public int ACTION_CREDIT_ADJUST = 3;
    public String HTTP_STATUS_200 = "200";
    public String RESULT_OK = "0,1,2";
    public String SUBSCRIBER_NOT_FOUND = "102";
    public String RESULT_100 = "100";
    // public String ACNT_NEW="126";
    public String ACNT_DISCONNECTED = "102";
    public String ACNT_BARRED_REFILL = "103";
    public String ACNT_TEMP_BLOCKED = "104";
    public String DEDICATED_ACNT_NOT_ALLOWED = "105";
    public String DEDICATED_ACNT_NEGATIVE = "106";
    public String MAX_LIMIT_EXCEED = "123";
    public String BELOW_MIN_BAL = "124";
    public String ACNT_NOT_ACTIVE = "126";
    public String DEDICATED_ACT_MAX_LIMIT_EXCEED = "153";

}

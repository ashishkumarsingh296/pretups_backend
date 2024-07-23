/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.vodaidea.vodafone.comverse;

/**
 * @author abhay.singh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ComverseI
{
	public int ACTION_ACCOUNT_DETAILS=1;
	public int ACTION_RECHARGE_CREDIT=2;
	public int ACTION_IMMEDIATE_DEBIT=3;
	public int ACTION_IMMEDIATE_CREDIT=4;
	public String HTTP_STATUS_200="200";
	public String RESULT_OK="VOUCHER_RECHARGE_SUCCESS_WITH_ERROR_,0";
	public String SUBSCRIBER_NOT_FOUND="CANNT_FIND_EXTERNALID";	
	public String VOUCHER_RECHARGE_ERROR_75="VOUCHER_RECHARGE_ERROR_75";
	public String VOUCHER_RECHARGE_ERROR_="VOUCHER_RECHARGE_ERROR_8,VOUCHER_RECHARGE_ERROR_9,VOUCHER_RECHARGE_ERROR_54";
	public int ACTION_LANGUAGE_CODE=5;
	//public String SUBSCRIBER_BARRED="";

}

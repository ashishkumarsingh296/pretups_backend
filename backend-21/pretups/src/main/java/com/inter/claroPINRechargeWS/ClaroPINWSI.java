/*
 * Created on June 18, 2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.claroPINRechargeWS;

/**
 * @author vipan.kumar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ClaroPINWSI
{
	public int ACTION_ACCOUNT_DETAILS=1;
	public int ACTION_RECHARGE_CREDIT=2;
	public int ACTION_IMMEDIATE_DEBIT=3;
	public int ACTION_IMMEDIATE_CREDIT=4;
	public String HTTP_STATUS_200="200";
	public String RESULT_OK="true,0";
	public String SUBSCRIBER_NOT_FOUND="4056";	
	public int ACTION_LANGUAGE_CODE=5;

	public String RESPONSE_SUCCESS="0";
	public String RESPONSE_TIMEOUT_INVALID="1";
	public String RESPONSE_MONTO_INVALID="2";
	public String RESPONSE_CLIENT_INVALID_RANGE="3";
	public String RESPONSE_CUSTOMER_INVALID="4";
	public String RESPONSE_MSISDN_INVALID="5";
	public String RESPONSE_MONTO_AMOUNT_INVALID="6";
	public String RESPONSE_ERRRO="7";

	public String RESPONSE_ERROR1="-1";
	public String RESPONSE_ERROR2="-2";
	public String RESPONSE_ERROR3="-3";
	public String RESPONSE_ERROR6="-6";
	
	public String TIPO_PIN="1";
	public String TIPO_PMD="2";
	
}
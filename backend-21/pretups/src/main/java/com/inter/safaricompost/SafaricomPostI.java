package com.inter.safaricompost;

/**
 * @(#)SafaricomPostI.java
 * Copyright(c) 2008, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 *Manisha Jain			09 june	2008	Initial creation
 * ------------------------------------------------------------------------------------------------
 * Handler class for the interface Post Paid billing System
 */
public interface SafaricomPostI 
{
	public int ACTION_ACCOUNT_INFO=90;
	public int ACTION_CREDIT=1;
	public String RESULT_OK="0";
	public String HTTP_STATUS_200="200";
	public String RESULT_ERROR_201 = "201";
	
	public String RESULT_ERROR_1 = "-1";//Not a valid postpaid number
	public String RESULT_ERROR_4 = "-4";//Not a valid postpaid number
	public String RESULT_ERROR_2 = "-2";//Unknown Error.
	public String RESULT_ERROR_3 = "-3";//Amount Paid is Negative or Zero.
	public String RESULT_ERROR_5 = "-5";//Not a valid postpaid number and Amount Paid is Negative or Zero 
}

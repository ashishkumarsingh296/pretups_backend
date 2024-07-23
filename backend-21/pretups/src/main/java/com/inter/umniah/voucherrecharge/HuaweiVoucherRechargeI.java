package com.inter.umniah.voucherrecharge;
/**
 * @(#)HuaweiVoucherRechargeI.java
 * Copyright(c) 2014, Bharti Telesoft Int. Public Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 *     Author				     Date			        History
 *-------------------------------------------------------------------------------------------------
 * Narendra Kumar		May 02, 2014		Initial Creation
 * ------------------------------------------------------------------------------------------------
 * This interface defined some constant corresponding to the HUAWEI interface.
 */
public interface HuaweiVoucherRechargeI {
	public int ACTION_ACCOUNT_INFO=0;
	public int ACTION_IMMEDIATE_DEBIT=1;
	public int ACTION_RECHARGE_CREDIT=2; //used for wsdl
	public String SUBSCRIBER_NOT_FOUND="1001";
	public String VOUCHER_ALREADY_USED="1026";
	public int ACTION_VOUCHER_RECHARGE=10;
	public String SUCESS ="405000000";	//used for wsdl
}

	
/* 
 * #RadixAirtelI.java
 *
 *------------------------------------------------------------------------------------------------
 *  Name                  Version		 Date            	History
 *-------------------------------------------------------------------------------------------------
 *  Mahindra Comviva       1.0     		04/09/2014         	Initial Creation
 *-------------------------------------------------------------------------------------------------
 *
 * Copyright(c) 2005 Comviva Technologies Ltd.
 *
 */

package com.inter.radix;

public interface RadixAirtelI {
	
	public int ACTION_SUBMIT_PROVISION=1;
	public int ACTION_RETRIEVE_PROVISION=2;
	public String HTTP_STATUS_200="200";
	public String SERVICE_CLASS_ALL="ALL";
	public String STATUS_QUEUED="0";
	public String STATUS_MISSING_PARAM="1";
	public String STATUS_INSUFFICIENT_FUNDS="2";
	public String STATUS_ERROR="3";
	public String STATUS_INVALID_PACKAGEID="4";
	public String STATUS_SUCCESS="5";
	public String STATUS_PACKAGE_CONFLICT="6";
	public String STATUS_INVALID_TXNID="8";
	public String STATUS_INVALID_REQID="9";
	public String RESULT_OK="";
	public String RESPONSE_QUEUED="queued";
	public String RESPONSE_SUCCESS="success";
	

}

/*
 * Created on Jun 10, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inter.safaricom;

/**
 * @author dhiraj.tiwari
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SafcomI {
	public int ACTION_ACCOUNT_INFO=0;
	public int ACTION_IMMEDIATE_DEBIT=1;
	public int ACTION_RECHARGE_CREDIT=2;
	public int ACTION_VALIDITY_ADJUST = 4;
	public int ACTION_TXN_CANCEL=5;
	public String RESULT_OK="1";
	public int RESULT_INT_OK=1;
}

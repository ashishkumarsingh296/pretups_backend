package com.btsl.pretups.inter.connection;
/**
* @(#)BTSLConnection.java
* Copyright(c) 2005, Bharti Telesoft Int. Public Ltd.
* All Rights Reserved
*-------------------------------------------------------------------------------------------------
* Author				Date			History
*-------------------------------------------------------------------------------------------------
* Abhijit Chauhan	   Oct 10,2005		Initial Creation
* ------------------------------------------------------------------------------------------------
*/
import java.io.BufferedReader;
import java.io.PrintWriter;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

public abstract class BTSLConnection
{
	private long _acquiredTime=0;
	private String _transactionID; 
	protected PrintWriter _out = null;
	protected BufferedReader _in = null;
	protected Log _log = LogFactory.getLog(this.getClass().getName());
	/**
	* @return
	*/
	public long getAcquiredTime() 
	{
		return _acquiredTime;
	}
	/**
	* @param l
	*/
	public void setAcquiredTime(long l) 
	{
		_acquiredTime = l;
	}
	/**
	* @return
	*/
	public PrintWriter getPrintWriter() 
	{
		return _out;
	}
	/**
	* @return
	*/
	public BufferedReader getBufferedReader() 
	{
		return _in;
	}
	/**
	 * 
	 * @return
	 */
	public String getTransactionID() {
		return _transactionID;
	}
	/**
	 * 
	 * @param transactionID
	 */
	public void setTransactionID(String transactionID) {
		_transactionID = transactionID;
	}
	abstract protected void setPrintWriter() throws Exception;
	abstract protected void setBufferedReader() throws Exception;
	abstract public void setTimeout(int p_timeout) throws Exception;
	abstract public void close() throws Exception;
	public int flush()  throws Exception
	{
		return 0;
	}
}

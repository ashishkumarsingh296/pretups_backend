package com.btsl.util;

/**
 *AESKeystoreQry 
 * @author sadhan.k
 *
 */
public interface AESKeystoreQry {

	/**
	 * getKeyFromDBFirst
	 * @return
	 */
	public String getKeyFromDBFirst();
	
	/**
	 * getKeyFromDBSecond
	 * @return
	 */
	public String getKeyFromDBSecond();
}

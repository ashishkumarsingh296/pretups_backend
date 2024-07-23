package com.btsl.pretups.user.businesslogic;

import java.io.Serializable;

public class UserTransferEnquiryVO implements Serializable {
	
	private static final long serialVersionUID = 2356376346208699091L;
	private String _userMSISDN;
	private String _serviceType;
	private int _totalCount;
	private int _successCount;
	private int _ambiguousCount;
	private int _underProcessCount;
	private int _failCount;
	
	public String toString() {
		
		StringBuilder  str  = new StringBuilder();
	    str = str.append("_userMSISDN="+_userMSISDN);
	    str = str.append(",_serviceType="+_serviceType);
	    str = str.append(",_totalCount="+_totalCount);
	    str = str.append(",_successCount="+_successCount);
	    str = str.append(",_ambiguousCount="+_ambiguousCount);
	    str = str.append(",_underProcessCount="+_underProcessCount);
	    str = str.append(",_failCount="+_failCount);
	    return str.toString();
	}
	
	/**
	 * @return the userID
	 */
	public String getUserMsisdn() {
		return _userMSISDN;
	}
	/**
	 * @param userID the userID to set
	 */
	public void setUserMsisdn(String userMSISDN) {
		this._userMSISDN = userMSISDN;
	}
	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return _serviceType;
	}
	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this._serviceType = serviceType;
	}
	/**
	 * @return the successCount
	 */
	public int getSuccessCount() {
		return _successCount;
	}
	/**
	 * @param successCount the successCount to set
	 */
	public void setSuccessCount(int successCount) {
		this._successCount = successCount;
	}
	/**
	 * @return the ambiguousCount
	 */
	public int getAmbiguousCount() {
		return _ambiguousCount;
	}
	/**
	 * @param ambiguousCount the ambiguousCount to set
	 */
	public void setAmbiguousCount(int ambiguousCount) {
		this._ambiguousCount = ambiguousCount;
	}
	/**
	 * @return the underProcessCount
	 */
	public int getUnderProcessCount() {
		return _underProcessCount;
	}
	/**
	 * @param underProcessCount the underProcessCount to set
	 */
	public void setUnderProcessCount(int underProcessCount) {
		this._underProcessCount = underProcessCount;
	}
	/**
	 * @return the failCount
	 */
	public int getFailCount() {
		return _failCount;
	}
	/**
	 * @param failCount the failCount to set
	 */
	public void setFailCount(int failCount) {
		this._failCount = failCount;
	}
	/**
	 * @return the _totalCount
	 */
	public int getTotalCount() {
		return _totalCount;
	}
	/**
	 * @param totalCount the _totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this._totalCount = totalCount;
	}
}

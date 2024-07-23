package simulator.loadtest.loadgenerator;

/* LoadRequestVO.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 Chetan Prakash Kothari             18/07/2008         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2008 Bharti Telesoft Ltd.
 * This class is used to collect the Request parameter details that are received for processing in LoadTest  
 */
public class LoadRequestVO {

	private long _requestStartTime;
	private long _requestEndTime;
	private String _msisdn;
	private String _requestType;
	private String _requestStatus;
	private String _nodeName;
	private String _httpStatus;
	private String _requestTransactionId;
	private String _responseTransactionId;
	/**
	 * @return Returns the _httpStatus.
	 */
	public String getHttpStatus() {
		return _httpStatus;
	}
	/**
	 * @param status The _httpStatus to set.
	 */
	public void setHttpStatus(String status) {
		_httpStatus = status;
	}
	/**
	 * @return Returns the _msisdn.
	 */
	public String getMsisdn() {
		return _msisdn;
	}
	/**
	 * @param _msisdn The _msisdn to set.
	 */
	public void setMsisdn(String _msisdn) {
		this._msisdn = _msisdn;
	}
	/**
	 * @return Returns the _nodeName.
	 */
	public String getNodeName() {
		return _nodeName;
	}
	/**
	 * @param name The _nodeName to set.
	 */
	public void setNodeName(String name) {
		_nodeName = name;
	}
	/**
	 * @return Returns the _requestEndTime.
	 */
	public long getRequestEndTime() {
		return _requestEndTime;
	}
	/**
	 * @param endTime The _requestEndTime to set.
	 */
	public void setRequestEndTime(long endTime) {
		_requestEndTime = endTime;
	}
	/**
	 * @return Returns the _requestStartTime.
	 */
	public long getRequestStartTime() {
		return _requestStartTime;
	}
	/**
	 * @param startTime The _requestStartTime to set.
	 */
	public void setRequestStartTime(long startTime) {
		_requestStartTime = startTime;
	}
	/**
	 * @return Returns the _requestStatus.
	 */
	public String getRequestStatus() {
		return _requestStatus;
	}
	/**
	 * @param status The _requestStatus to set.
	 */
	public void setRequestStatus(String status) {
		_requestStatus = status;
	}
	/**
	 * @return Returns the _requestType.
	 */
	public String getRequestType() {
		return _requestType;
	}
	/**
	 * @param type The _requestType to set.
	 */
	public void setRequestType(String type) {
		_requestType = type;
	}
	/**
	 * @return Returns the _requestTransactionId.
	 */
	public String getRequestTransactionId() {
		return _requestTransactionId;
	}
	/**
	 * @param id The _requestTransactionId to set.
	 */
	public void setRequestTransactionId(String id) {
		_requestTransactionId = id;
	}
	
	/**
	 * @return Returns the _responseTransactionId.
	 */
	public String getResponseTransactionId() {
		return _responseTransactionId;
	}
	/**
	 * @param id The _responseTransactionId to set.
	 */
	public void setResponseTransactionId(String id) {
		_responseTransactionId = id;
	}
}

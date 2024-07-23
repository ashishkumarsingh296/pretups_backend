package simulator.loadtest.report;

/* LoadRequestVO.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 Chetan Prakash Kothari             18/07/2008         Initial Creation
 *------------------------------------------------------------------------
 * Copyright (c) 2008 Bharti Telesoft Ltd.
 * This class is used to collect the Request parameter details that are received for processing in LoadTest  
 */
public class LoadRequestVO {

	private long requestStartTime;
	private long requestEndTime;
	private long timeDiff;
	private String msisdn;
	private String requestType;
	private String requestStatus;
	private String nodeName;
	private String httpStatus;
	private String requestTransactionId;
	private String responseTransactionId;
	
	

	/**
	 * @return Returns the httpStatus.
	 */
	public String getHttpStatus() {
		return httpStatus;
	}
	/**
	 * @param httpStatus The httpStatus to set.
	 */
	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}
	/**
	 * @return Returns the msisdn.
	 */
	public String getMsisdn() {
		return msisdn;
	}
	/**
	 * @param msisdn The msisdn to set.
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	/**
	 * @return Returns the nodeName.
	 */
	public String getNodeName() {
		return nodeName;
	}
	/**
	 * @param nodeName The nodeName to set.
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * @return Returns the requestEndTime.
	 */
	public long getRequestEndTime() {
		return requestEndTime;
	}
	/**
	 * @param requestEndTime The requestEndTime to set.
	 */
	public void setRequestEndTime(long requestEndTime) {
		this.requestEndTime = requestEndTime;
	}
	/**
	 * @return Returns the requestStartTime.
	 */
	public long getRequestStartTime() {
		return requestStartTime;
	}
	/**
	 * @param requestStartTime The requestStartTime to set.
	 */
	public void setRequestStartTime(long requestStartTime) {
		this.requestStartTime = requestStartTime;
	}
	/**
	 * @return Returns the requestStatus.
	 */
	public String getRequestStatus() {
		return requestStatus;
	}
	/**
	 * @param requestStatus The requestStatus to set.
	 */
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	/**
	 * @return Returns the requestTransactionId.
	 */
	public String getRequestTransactionId() {
		return requestTransactionId;
	}
	/**
	 * @param requestTransactionId The requestTransactionId to set.
	 */
	public void setRequestTransactionId(String requestTransactionId) {
		this.requestTransactionId = requestTransactionId;
	}
	/**
	 * @return Returns the requestType.
	 */
	public String getRequestType() {
		return requestType;
	}
	/**
	 * @param requestType The requestType to set.
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	/**
	 * @return Returns the responseTransactionId.
	 */
	public String getResponseTransactionId() {
		return responseTransactionId;
	}
	/**
	 * @param responseTransactionId The responseTransactionId to set.
	 */
	public void setResponseTransactionId(String responseTransactionId) {
		this.responseTransactionId = responseTransactionId;
	}
	/**
	 * @return Returns the timeDiff.
	 */
	public long getTimeDiff() {
		return timeDiff;
	}
	/**
	 * @param timeDiff The timeDiff to set.
	 */
	public void setTimeDiff(long timeDiff) {
		this.timeDiff = timeDiff;
	}
}

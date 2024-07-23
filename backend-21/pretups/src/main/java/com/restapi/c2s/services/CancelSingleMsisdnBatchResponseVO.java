package com.restapi.c2s.services;

import com.btsl.common.ErrorMap;

public class CancelSingleMsisdnBatchResponseVO{
	private String service;
    private String messageCode;
    private String message;
    private ErrorMap errorMap;
	private String scheduleBatchId;
	private String status;
	private long canceledRecords = 0;
	

	public String getScheduleBatchId() {
		return scheduleBatchId;
	}

	public void setScheduleBatchId(String scheduleBatchId) {
		this.scheduleBatchId = scheduleBatchId;
	}

	

	public String getStatus() {
		return status;
	}


	public void setStatus(String txnstatus) {
		this.status = txnstatus;
	}


	public String getMessageCode() {
		return messageCode;
	}


	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public ErrorMap getErrorMap() {
		return errorMap;
	}


	public void setErrorMap(ErrorMap errorMap) {
		this.errorMap = errorMap;
	}


	public long getCanceledRecords() {
		return canceledRecords;
	}

	public void setCanceledRecords(long canceledRecords) {
		this.canceledRecords = canceledRecords;
	}
	
	

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
	public String toString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("CancelSingleMsisdnBatchResponseVO [")
		.append("service=" + service)
		.append("messageCode=" + messageCode)
		.append("message=" + message)
		.append("errorMap=" + errorMap)
		.append("scheduleBatchId=" + scheduleBatchId)
		.append("txnstatus=" + status)
		.append("canceledRecords=" + canceledRecords);
		return sbf.toString();
	}


	
	
	

}

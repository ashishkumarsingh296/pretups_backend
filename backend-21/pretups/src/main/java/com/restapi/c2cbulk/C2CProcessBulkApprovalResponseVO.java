package com.restapi.c2cbulk;

import com.btsl.common.ErrorMap;

public class C2CProcessBulkApprovalResponseVO {


	
	private String fileAttachment;

	private String fileName;

    public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	private String messageCode;
    private String message;
    private ErrorMap errorMap;
	private String scheduleBatchId;
	private String status;
	private long numberOfRecords = 0;
	
	public long getNumberOfRecords() {
		return numberOfRecords;
	}
	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

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


	@Override
	public String toString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("C2SBulkRechargeResponseVO [")
		.append("messageCode=" + messageCode)
		.append("message=" + message)
		.append("errorMap=" + errorMap)
		.append("scheduleBatchId=" + scheduleBatchId)
		.append("txnstatus=" + status);
		return sbf.toString();
	}


	
	
	


	
}
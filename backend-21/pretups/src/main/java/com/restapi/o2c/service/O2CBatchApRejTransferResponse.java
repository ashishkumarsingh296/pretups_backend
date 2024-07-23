package com.restapi.o2c.service;

import com.btsl.common.BaseResponseMultiple;

public class O2CBatchApRejTransferResponse extends BaseResponseMultiple {
	private String batchID;
	private String fileName;

	private String fileAttachment;
	private String fileType;
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileAttachment() {
		return fileAttachment;
	}

	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

	private String noOfRecords = null;
	
	public String getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(String _noOfRecords) {
		this.noOfRecords = _noOfRecords;
	}

	public String getProcessedRecs() {
		return processedRecs;
	}

	public void setProcessedRecs(String _processedRecs) {
		this.processedRecs = _processedRecs;
	}

	private String processedRecs = null;
	
	public String getBatchID() {
		return batchID;
	}

	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}
	

}

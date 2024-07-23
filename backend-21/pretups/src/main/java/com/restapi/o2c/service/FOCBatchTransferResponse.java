package com.restapi.o2c.service;

import com.btsl.common.BaseResponseMultiple;

public class FOCBatchTransferResponse extends BaseResponseMultiple{
	
	
	private String fileAttachment;
	private String batchID;
	public String getBatchID() {
		return batchID;
	}
	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}


	private String fileName;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	@Override
	public String toString() {
		return "FOCBatchTransferResponse [fileAttachment=" + fileAttachment
				+ ", batchID=" + batchID + ", fileName=" + fileName + "]";
	}

	
	
}

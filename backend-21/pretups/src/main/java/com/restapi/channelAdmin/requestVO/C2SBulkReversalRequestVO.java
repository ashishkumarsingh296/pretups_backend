package com.restapi.channelAdmin.requestVO;

public class C2SBulkReversalRequestVO {

	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAttachment() {
		return attachment;
	}
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}
	
		

	String batchName;
	//filename
	String fileName;
	//Base64 encoded string
	String attachment;
	String fileType;
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	@Override
	public String toString() {
		return "C2SBulkReversalRequestVO [batchName=" + batchName + ", fileName=" + fileName + ", attachment=" + attachment
				+ "]";
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

}

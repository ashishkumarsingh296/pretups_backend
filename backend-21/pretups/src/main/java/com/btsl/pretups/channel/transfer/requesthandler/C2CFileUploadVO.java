package com.btsl.pretups.channel.transfer.requesthandler;



public class C2CFileUploadVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "xlsx", required = true, description="File Type(csv, xls, xlsx")
	private String fileType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "c2cBatchTransfer", required = true, description="File Name")
	private String fileName;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true, description="Base64 Encoded File as String")
	private String fileAttachment;
	@io.swagger.v3.oas.annotations.media.Schema(example = "true", required = true, description="true/false")
	private String fileUploaded;
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
	public String getFileUploaded() {
		return fileUploaded;
	}
	public void setFileUploaded(String fileUploaded) {
		this.fileUploaded = fileUploaded;
	}
	@Override
	public String toString() {
		return "C2CFileUploadVO [fileType=" + fileType + ", fileName=" + fileName + ", fileAttachment=" + fileAttachment
				+ ", fileUploaded=" + fileUploaded + "]";
	}
	
	

}

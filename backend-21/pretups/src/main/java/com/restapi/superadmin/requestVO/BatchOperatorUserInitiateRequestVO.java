package com.restapi.superadmin.requestVO;



public class BatchOperatorUserInitiateRequestVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "xls", required = true, description="File Type(csv, xls, xlsx")
	private String fileType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Batch Operator User Initiate", required = true, description="File Name")
	private String fileName;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true, description="Base64 Encoded File as String")
	private String fileAttachment;
	
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
	@Override
	public String toString() {
		return "BatchOperatorUserInitiateRequestVO [fileType=" + fileType + ", fileName=" + fileName + ", fileAttachment="
				+ fileAttachment + "]";
	}
}

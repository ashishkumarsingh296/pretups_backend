package com.restapi.channelAdmin.requestVO;



public class BulkModifyUserRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "xlsx", required = true, description="File Type(csv, xls, xlsx")
	private String fileType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Bulk Modify User List", required = true, description="File Name")
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
		return "BulkModifyUserRequest [fileType=" + fileType + ", fileName=" + fileName + ", fileAttachment="
				+ fileAttachment + "]";
	}
}

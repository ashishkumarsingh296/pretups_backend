package com.restapi.user.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class C2CFileUploadApiRequest {
	@io.swagger.v3.oas.annotations.media.Schema(example = "xlsx", required = true, description="File Type(csv, xls, xlsx")
	private String fileType;
	@io.swagger.v3.oas.annotations.media.Schema(example = "c2cBatchTransfer", required = true, description="File Name")
	private String fileName;
	@io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true, description="Base64 Encoded File as String")
	private String fileAttachment;
	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true)
    private String pin;
	@JsonProperty("batchName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "test13243234", required = true)
	private String batchName;
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getLanguage1() {
		return language1;
	}
	public void setLanguage1(String language1) {
		this.language1 = language1;
	}
	public String getLanguage2() {
		return language2;
	}
	public void setLanguage2(String language2) {
		this.language2 = language2;
	}
	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "english")
	private String language1;
	@JsonProperty("language2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "spanish")
	private String language2;
	
    public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
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
		return "C2CFileUploadApiRequest [fileType=" + fileType + ", fileName=" + fileName + ", fileAttachment="
				+ fileAttachment + "]";
	}
	
	
	

}

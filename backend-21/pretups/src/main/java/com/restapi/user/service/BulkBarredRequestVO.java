package com.restapi.user.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class BulkBarredRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("file")
	private String file;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("fileName")
	private String fileName;

	@io.swagger.v3.oas.annotations.media.Schema(example = "SENDER/RECEIVER", required = true)
	@JsonProperty("userType")
	private String userType;
	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("fileType")
	private String fileType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "SL024", required = true)
	@JsonProperty("barringType")
	private String barringType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Bar_test", required = true)
	@JsonProperty("barringTypeName")
	private String barringTypeName;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("barringReason")
	private String barringReason;

	@io.swagger.v3.oas.annotations.media.Schema(example = "C2S", required = true)
	@JsonProperty("module")
	private String module;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getBarringType() {
		return barringType;
	}

	public void setBarringType(String barringType) {
		this.barringType = barringType;
	}

	public String getBarringReason() {
		return barringReason;
	}

	public void setBarringReason(String barringReason) {
		this.barringReason = barringReason;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getBarringTypeName() {
		return barringTypeName;
	}

	public void setBarringTypeName(String barringTypeName) {
		this.barringTypeName = barringTypeName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BulkBarredRequestVO [file=");
		builder.append(file);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", userType=");
		builder.append(userType);
		builder.append(", fileType=");
		builder.append(fileType);
		builder.append(", barringType=");
		builder.append(barringType);
		builder.append(", barringTypeName=");
		builder.append(barringTypeName);
		builder.append(", barringReason=");
		builder.append(barringReason);
		builder.append(", module=");
		builder.append(module);
		builder.append("]");
		return builder.toString();
	}

}

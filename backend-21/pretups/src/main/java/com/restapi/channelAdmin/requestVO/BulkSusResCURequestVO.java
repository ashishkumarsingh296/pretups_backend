package com.restapi.channelAdmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;



public class BulkSusResCURequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("file")
	private String file;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("fileName")
	private String fileName;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("fileType")
	private String fileType;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "SR/RR", required = true)
	@JsonProperty("operationType")
	private String operationType;

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

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BulkSusResCURequestVO [file=").append(file).append(", fileName=").append(fileName)
				.append(", fileType=").append(fileType).append(", operationType=").append(operationType).append("]");
		return builder.toString();
	}
	
	
	
}

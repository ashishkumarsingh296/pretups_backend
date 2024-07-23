package com.restapi.channelAdmin.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;



public class BulkUserUploadRequestVO {
	
	@JsonProperty("batchName")
	private String batchName;
	
	@JsonProperty("domainCode")
	private String domainCode;
	
	@JsonProperty("geographyCode")
	private String geographyCode;
	
	@JsonProperty("file")
	private String file;
	
	@JsonProperty("fileName")
	private String fileName;
	
	@JsonProperty("fileType")
	private String fileType;
	
	@JsonProperty("file")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */, description="file")
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	@JsonProperty("fileName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SCH00001"/* , defaultValue = "" */,description ="fileName",required = true)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@JsonProperty("fileType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "XLSX"/* , defaultValue = "" */,description ="fileType",required = true)
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public String getGeographyCode() {
		return geographyCode;
	}

	public void setGeographyCode(String geographyCode) {
		this.geographyCode = geographyCode;
	}

}

package com.restapi.c2cbulk;

import com.fasterxml.jackson.annotation.JsonProperty;

public class C2CBulkAppProcessData {

    @JsonProperty("batchId")
    private String batchId;
	
	@JsonProperty("file")
	private String file;
	
	@JsonProperty("fileName")
	private String fileName;
	
	@JsonProperty("fileType")
	private String fileType;
	
	@JsonProperty("language1")
	private String language1;
	
	@JsonProperty("language2")
	private String language2;

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

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
	

	
}

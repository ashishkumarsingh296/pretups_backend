package com.restapi.c2cbulk;

import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2CProcessBulkRequestVO extends OAuthUserData {


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

	@JsonProperty("batchId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "batchId")
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
    
	@JsonProperty("file")
	@io.swagger.v3.oas.annotations.media.Schema(example = "file")
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@JsonProperty("fileName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "fileName")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@JsonProperty("fileType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "xls/csv/xlsx")
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "english")
	public String getLanguage1() {
		return language1;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	@JsonProperty("language2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "spanish")
	public String getLanguage2() {
		return language2;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}
	
}

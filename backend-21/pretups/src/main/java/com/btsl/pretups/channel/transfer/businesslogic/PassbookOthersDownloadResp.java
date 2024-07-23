package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PassbookOthersDownloadResp extends BaseResponseMultiple {


	@JsonProperty("fileName")
	private String fileName;
	

	@JsonProperty("fileType")
	private String fileType;	
	
	
	@JsonProperty("fileData")
	private String fileData;
	
	@JsonProperty("totalRecords")
	private String totalRecords;
	
	//This is for processing only online dowload path[offline not required],
	//but will make it null in controller class to not expose file path.
	@JsonProperty("filePath")
	private String filePath; 
	
	
	
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
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
	public String getFileData() {
		return fileData;
	}
	public void setFileData(String fileData) {
		this.fileData = fileData;
	}
	

	public String getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PassbookDownloadResp")
				.append(", fileName=").append(fileName).append(", fileType=").append(fileType).append(", fileData=").append(fileData).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	
	
}

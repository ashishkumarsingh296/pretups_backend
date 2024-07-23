package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class C2STransferCommDownloadResp extends BaseResponseMultiple {


	@JsonProperty("fileName")
	private String fileName;
	

	@JsonProperty("fileType")
	private String fileType;	
	
	
	@JsonProperty("fileData")
	private String fileData;
	
	@JsonProperty("totalRecords")
	private String totalRecords;
	
	
	
	
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
	
	
}

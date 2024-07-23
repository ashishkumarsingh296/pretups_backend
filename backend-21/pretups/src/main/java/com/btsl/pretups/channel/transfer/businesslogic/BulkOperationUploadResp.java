package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class BulkOperationUploadResp extends BaseResponseMultiple {


	@JsonProperty("fileName")
	private String fileName;
	

	
	
		
	
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PassbookDownloadResp")
				.append(", fileName=").append(fileName).append(", fileType=").append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	public String getFileName() {
		return fileName;
	}
	

	
	
	
}

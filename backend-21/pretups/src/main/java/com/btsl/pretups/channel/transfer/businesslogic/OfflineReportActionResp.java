package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class OfflineReportActionResp extends BaseResponseMultiple {



	
	
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
		builder.append("OfflineReportActionResp")
				.append(", messageCode=").append(super.getMessageCode()).append(", message=").append(super.getMessage())
				.append("]");
		return builder.toString();
	}
	
	
}

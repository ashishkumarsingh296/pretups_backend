package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class PinPassHistorySearchResp extends BaseResponseMultiple {

	@JsonProperty("pinPassHistSearchVOList")
	List<PinPassHistSearchRecordVO> pinPassHistSearchVOList;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	public List<PinPassHistSearchRecordVO> getPinPassHistSearchVOList() {
		return pinPassHistSearchVOList;
	}
	public void setPinPassHistSearchVOList(List<PinPassHistSearchRecordVO> pinPassHistSearchVOList) {
		this.pinPassHistSearchVOList = pinPassHistSearchVOList;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PinPassHistorySearchResp")
				.append(",pinPassHistSearchVOList ").append(pinPassHistSearchVOList.toString()).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	

	
	
	
}

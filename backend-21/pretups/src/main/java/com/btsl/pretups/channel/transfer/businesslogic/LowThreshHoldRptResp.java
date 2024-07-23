package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LowThreshHoldRptResp extends BaseResponseMultiple {

	@JsonProperty("LowThreshHoldDataList")
	List<LowThreshHoldRecordVO> lowThreshHoldDataList = null;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public List<LowThreshHoldRecordVO> getLowThreshHoldDataList() {
		return lowThreshHoldDataList;
	}

	public void setLowThreshHoldDataList(List<LowThreshHoldRecordVO> lowThreshHoldDataList) {
		this.lowThreshHoldDataList = lowThreshHoldDataList;
	}

	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LowThreshHoldRptResp").append(", lowThreshHoldDataList=").append(lowThreshHoldDataList)
				.append(", additionalProperties=").append(additionalProperties).append("]");
		return builder.toString();
	}

}

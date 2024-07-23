package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalCommissionSummryC2SResp extends BaseResponseMultiple {

	@JsonProperty("addtnlcommissionSummaryList")
	List<AddtnlCommSummaryRecordVO> addtnlcommissionSummaryList;
	private String totalTransactionCount;
	

	private String totalDiffAmount;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	
	public String getTotalTransactionCount() {
		return totalTransactionCount;
	}

	public void setTotalTransactionCount(String totalTransactionCount) {
		this.totalTransactionCount = totalTransactionCount;
	}
	

	public String getTotalDiffAmount() {
		return totalDiffAmount;
	}

	public void setTotalDiffAmount(String totalDiffAmount) {
		this.totalDiffAmount = totalDiffAmount;
	}

	public List<AddtnlCommSummaryRecordVO> getAddtnlcommissionSummaryList() {
		return addtnlcommissionSummaryList;
	}

	public void setAddtnlcommissionSummaryList(List<AddtnlCommSummaryRecordVO> addtnlcommissionSummaryList) {
		this.addtnlcommissionSummaryList = addtnlcommissionSummaryList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AdditionalCommissionSummryC2SResp").append(",addtnlcommissionSummaryList ")
				.append(addtnlcommissionSummaryList.toString()).append(", additionalProperties=")
				.append(additionalProperties).append("]");
		return builder.toString();
	}

}

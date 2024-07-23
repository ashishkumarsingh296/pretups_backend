package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class C2StransferCommisionResp extends BaseResponseMultiple {

	@JsonProperty("c2stransferCommissionList")
	List<C2StransferCommisionRecordVO> c2stransferCommissionList;
	
	@JsonProperty("c2StransferCommSummryData")
	C2StransferCommSummryData c2StransferCommSummryData;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	
	public List<C2StransferCommisionRecordVO> getC2stransferCommissionList() {
		return c2stransferCommissionList;
	}
	public void setC2stransferCommissionList(List<C2StransferCommisionRecordVO> c2stransferCommissionList) {
		this.c2stransferCommissionList = c2stransferCommissionList;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2StransferCommisionResp")
				.append(",c2stransferCommissionList ").append(c2stransferCommissionList.toString()).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	public C2StransferCommSummryData getC2StransferCommSummryData() {
		return c2StransferCommSummryData;
	}
	public void setC2StransferCommSummryData(C2StransferCommSummryData c2StransferCommSummryData) {
		this.c2StransferCommSummryData = c2StransferCommSummryData;
	}
	

	
	
	
}

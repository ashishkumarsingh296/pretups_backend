package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class C2CtransferCommisionResp extends BaseResponseMultiple {

	@JsonProperty("c2ctransferCommissionList")
	List<C2CtransferCommisionRecordVO> c2ctransferCommissionList;

	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	

	public List<C2CtransferCommisionRecordVO> getC2ctransferCommissionList() {
		return c2ctransferCommissionList;
	}
	public void setC2ctransferCommissionList(List<C2CtransferCommisionRecordVO> c2ctransferCommissionList) {
		this.c2ctransferCommissionList = c2ctransferCommissionList;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2CtransferCommisionResp")
				.append(",c2ctransferCommissionList ").append(c2ctransferCommissionList.toString()).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	
	
	
	
	
	
}

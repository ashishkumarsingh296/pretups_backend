package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class O2CtransferDetSearchResp extends BaseResponseMultiple {

	@JsonProperty("o2cTransferDetailList")
	List<O2CtransferDetRecordVO> o2cTransferDetailList;

	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	

	
	
	public List<O2CtransferDetRecordVO> getO2cTransferDetailList() {
		return o2cTransferDetailList;
	}
	public void setO2cTransferDetailList(List<O2CtransferDetRecordVO> o2cTransferDetailList) {
		this.o2cTransferDetailList = o2cTransferDetailList;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("C2CtransferCommisionResp")
				.append(",o2cTransferDetailList ").append(o2cTransferDetailList.toString()).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	
	
	
	
	
	
}

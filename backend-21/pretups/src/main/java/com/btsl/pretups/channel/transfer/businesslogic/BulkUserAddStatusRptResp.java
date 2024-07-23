package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class BulkUserAddStatusRptResp extends BaseResponseMultiple {

	@JsonProperty("bulkUserAddStatusRptList")
	List<BatchesVO> bulkUserAddStatusRptList;

	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
	

	
	
	
	public List<BatchesVO> getBulkUserAddStatusRptList() {
		return bulkUserAddStatusRptList;
	}
	public void setBulkUserAddStatusRptList(List<BatchesVO> bulkUserAddStatusRptList) {
		this.bulkUserAddStatusRptList = bulkUserAddStatusRptList;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BulkUserAddStatusRptResp")
				.append(",bulkUserAddStatusRptList ").append(bulkUserAddStatusRptList.toString()).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	
	
	
	
	
	
}

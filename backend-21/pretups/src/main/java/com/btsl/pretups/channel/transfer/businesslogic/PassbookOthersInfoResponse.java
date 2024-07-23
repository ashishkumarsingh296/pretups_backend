package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PassbookOthersInfoResponse extends BaseResponseMultiple {


	@JsonProperty("passbookList")
	List<PassbookOthersRecordVO> passbookOthersRecordVO = null;
	
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
		builder.append("PassbookOthersRecordVO")
				.append(", passbookSearchRecordVO=").append(passbookOthersRecordVO).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}
	public List<PassbookOthersRecordVO> getPassbookOthersRecordVO() {
		return passbookOthersRecordVO;
	}
	public void setPassbookOthersRecordVO(List<PassbookOthersRecordVO> passbookOthersRecordVO) {
		this.passbookOthersRecordVO = passbookOthersRecordVO;
	}
	
	
	
	
	
	
}

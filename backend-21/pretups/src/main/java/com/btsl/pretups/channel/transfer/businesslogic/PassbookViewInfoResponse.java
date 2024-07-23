package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.user.businesslogic.PassbookDetailsVO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PassbookViewInfoResponse extends BaseResponseMultiple {

	@JsonProperty("fromDate")
	private String fromDate;
	@JsonProperty("toDate")
	private String toDate;
	@JsonProperty("passbook")
	LinkedHashMap<String, PassbookDetailsVO> passbook = null;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public LinkedHashMap<String, PassbookDetailsVO> getPassbook() {
		return passbook;
	}
	public void setPassbook(LinkedHashMap<String, PassbookDetailsVO> passbook) {
		this.passbook = passbook;
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
		builder.append("PassbookViewInfoResponse [fromDate=").append(fromDate).append(", toDate=").append(toDate)
				.append(", passbook=").append(passbook).append(", additionalProperties=").append(additionalProperties)
				.append("]");
		return builder.toString();
	}

	
	
	
}

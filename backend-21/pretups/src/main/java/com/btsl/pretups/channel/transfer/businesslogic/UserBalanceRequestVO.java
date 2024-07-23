package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserBalanceRequestVO {
	
	 @JsonProperty("fromDate")
     private String fromDate;
     @JsonProperty("toDate")
     private String toDate;
	
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
}

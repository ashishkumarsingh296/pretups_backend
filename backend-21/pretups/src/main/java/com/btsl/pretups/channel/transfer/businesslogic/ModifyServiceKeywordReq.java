package com.btsl.pretups.channel.transfer.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyServiceKeywordReq extends AddServiceKeywordReq {
	
	@JsonProperty("serviceKeywordID")
	private String serviceKeywordID;


	public String getServiceKeywordID() {
		return serviceKeywordID;
	}


	public void setServiceKeywordID(String serviceKeywordID) {
		this.serviceKeywordID = serviceKeywordID;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		super.toString();
		builder.append("ModifyServiceKeywordReq").append(", serviceKeywordID=").append(serviceKeywordID).append("]");
		return builder.toString();
	}

}

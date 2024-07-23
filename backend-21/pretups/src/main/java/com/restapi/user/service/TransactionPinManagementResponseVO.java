package com.restapi.user.service;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionPinManagementResponseVO extends BaseResponse {
	
	
	@JsonProperty("pinChangeRequired")
	private Boolean pinChangeRequired;
	
	public TransactionPinManagementResponseVO() {
		super();
	}

	public Boolean getPinChangeRequired() {
		return pinChangeRequired;
	}

	public void setPinChangeRequired(Boolean pinChangeRequired) {
		this.pinChangeRequired = pinChangeRequired;
	}

	@Override
	public String toString() {
		return "TransactionPinManagementResponseVO [pinChangeRequired=" + pinChangeRequired + "]";
	}
	
	
	

}

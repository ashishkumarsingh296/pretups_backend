package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2SBulkEvdRechargeRequestVO extends OAuthUser {
	
		
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true, description="Request Data")
	C2SBulkRechargeDetails data;

	@JsonProperty("data")
	public C2SBulkRechargeDetails getData() {
		return data;
	}

	public void setData(C2SBulkRechargeDetails data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "C2SBulkRechargeRequestVO [data=" + data + "]";
	}
	
	
}

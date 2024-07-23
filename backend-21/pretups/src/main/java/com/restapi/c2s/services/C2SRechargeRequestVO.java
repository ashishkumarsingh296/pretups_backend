package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2SRechargeRequestVO extends OAuthUser {
	
		
	
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true, description="Request Data")
	C2SRechargeDetails data;

	@JsonProperty("data")
	public C2SRechargeDetails getData() {
		return data;
	}

	public void setData(C2SRechargeDetails data) {
		this.data = data;
	}
	

}

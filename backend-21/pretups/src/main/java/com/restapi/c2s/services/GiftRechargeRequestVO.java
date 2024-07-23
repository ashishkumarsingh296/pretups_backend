package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GiftRechargeRequestVO extends OAuthUser{
	
		
	@JsonProperty("data")
	GiftRechargeDetails data;

	@JsonProperty("data")
	public GiftRechargeDetails getData() {
		return data;
	}

	public void setData(GiftRechargeDetails data) {
		this.data = data;
	}
	

}

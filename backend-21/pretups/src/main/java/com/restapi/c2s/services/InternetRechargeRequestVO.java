package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InternetRechargeRequestVO extends OAuthUser{
	
		
	@JsonProperty("data")
	InternetRechargeDetails data;

	@JsonProperty("data")
	public InternetRechargeDetails getData() {
		return data;
	}

	public void setData(InternetRechargeDetails data) {
		this.data = data;
	}
	

}

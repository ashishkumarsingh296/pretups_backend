package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUser;

import com.fasterxml.jackson.annotation.JsonProperty;



public class MvdRequestVO extends OAuthUser {
	
		
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true, description="Request Data")
	MvdDetails data;

	@JsonProperty("data")
	public MvdDetails getData() {
		return data;
	}

	public void setData(MvdDetails data) {
		this.data = data;
	}
	

}

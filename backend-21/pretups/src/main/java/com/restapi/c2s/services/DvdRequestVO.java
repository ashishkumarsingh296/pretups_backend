package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class DvdRequestVO extends OAuthUser {
	
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true, description="Request Data")
	DvdDetails data;

	@JsonProperty("data")
	public DvdDetails getData() {
		return data;
	}

	public void setData(DvdDetails data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "DvdRequestVO [data=" + data + "]";
	}

	
}

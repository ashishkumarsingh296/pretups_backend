package com.restapi.c2s.services;

import java.util.List;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2SRechargeReversalRequestVO extends OAuthUser {
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true, description="Request Data")
	List<C2SRechargeReversalDetails> data;

	@JsonProperty("data")
	public List<C2SRechargeReversalDetails> getDataRev() {
		return data;
	}

	public void setDataRev(List<C2SRechargeReversalDetails> data) {
		this.data = data;
	}
	
	@JsonProperty("senderPin")
	private String senderPin;

	@JsonProperty("senderPin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */, description="PIN")
	public String getPin1() {
		return senderPin;
	}

	public void setPin1(String senderPin) {
		this.senderPin = senderPin;
	}
	 @Override
	    public String toString() {
	    	StringBuilder sb = new StringBuilder();
	        return (sb.append("data = ").append(data)
	        		.append("senderPin").append( senderPin)
	        		).toString();
	    }
	
	
}

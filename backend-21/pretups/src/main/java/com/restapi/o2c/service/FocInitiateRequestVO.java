package com.restapi.o2c.service;

import java.util.ArrayList;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class FocInitiateRequestVO extends OAuthUser {
	
	@JsonProperty("data")
    private ArrayList<FocTransferInitaiateReqData> datafoc;

	@io.swagger.v3.oas.annotations.media.Schema(required = true, description = "FOC stock approval requestdata")
	public ArrayList<FocTransferInitaiateReqData> getDatafoc() {
		return datafoc;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FocInitiateRequestVO [datafoc=");
		builder.append(datafoc);
		builder.append("]");
		return builder.toString();
	}

	public void setDatafoc(ArrayList<FocTransferInitaiateReqData> datafoc) {
		this.datafoc = datafoc;
	}
	
}

package com.restapi.c2s.services;

import java.util.ArrayList;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class CancelBatchC2SRequestVO {
	
		
	@JsonProperty("data")
	@io.swagger.v3.oas.annotations.media.Schema(required =true, description="Request Data")
	ArrayList<String> batchIDS;

public ArrayList<String> getBatchIDS() {
		return batchIDS;
	}


	public void setBatchIDS(ArrayList<String> batchIDS) {
		this.batchIDS = batchIDS;
	}


	@Override
	public String toString() {
		return "CancelBatchC2SRequestVO [data=" + batchIDS + "]";
	}
	
	
}

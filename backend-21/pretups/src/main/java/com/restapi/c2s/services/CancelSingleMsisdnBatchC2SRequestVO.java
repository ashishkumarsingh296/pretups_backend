package com.restapi.c2s.services;

import java.util.ArrayList;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class CancelSingleMsisdnBatchC2SRequestVO{
	@JsonProperty("msisdnList")
	@io.swagger.v3.oas.annotations.media.Schema(required =true, description="Msisdn List")
	ArrayList<String> msisdnList;
	
	@JsonProperty("batchId")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description="External Network Code")
	String batchId;
	

	public ArrayList<String> getMsisdnList() {
		return msisdnList;
	}

	public void setMsisdnList(ArrayList<String> msisdnList) {
		this.msisdnList = msisdnList;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	@Override
	public String toString() {
		return "CancelSingleMsisdnBatchC2SRequestVO [msisdnList=" + msisdnList
				+ ", batchId=" + batchId + "]";
	}

}

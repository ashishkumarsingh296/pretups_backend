package com.restapi.networkadmin.requestVO;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UpdateServiceClassPreferenceReqVO {

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceUpdateList")
	private ArrayList<UpdateServiceClassPreferenceVO> preferenceUpdateList;

	
	
	
	
	public ArrayList<UpdateServiceClassPreferenceVO> getPreferenceUpdateList() {
		return preferenceUpdateList;
	}

	public void setPreferenceUpdateList(ArrayList<UpdateServiceClassPreferenceVO> preferenceUpdateList) {
		this.preferenceUpdateList = preferenceUpdateList;
	}
}

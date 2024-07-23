package com.restapi.networkadmin.requestVO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.restapi.preferences.requestVO.UpdateSystemPreferenceVO;



public class UpdateNetworkPreferenceReqVO {

	
	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceUpdateList")
	private ArrayList<UpdateNetworkPreferenceVO> preferenceUpdateList;

	
	
	
	
	public ArrayList<UpdateNetworkPreferenceVO> getPreferenceUpdateList() {
		return preferenceUpdateList;
	}

	public void setPreferenceUpdateList(ArrayList<UpdateNetworkPreferenceVO> preferenceUpdateList) {
		this.preferenceUpdateList = preferenceUpdateList;
	}
			
}


package com.restapi.superadmin.networkmanagement.requestVO;

import java.util.ArrayList;

import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class ModifyNetworkRequestVO {
	
	@Schema(example = "GJ", required = true)
	@JsonProperty("networkList")
	private ArrayList<NetworkVO> networkList;

	public ArrayList<NetworkVO> getNetworkList() {
		return networkList;
	}

	public void setNetworkList(ArrayList<NetworkVO> networkList) {
		this.networkList = networkList;
	}
	
}

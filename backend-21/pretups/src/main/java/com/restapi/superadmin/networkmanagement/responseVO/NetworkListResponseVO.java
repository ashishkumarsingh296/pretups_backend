package com.restapi.superadmin.networkmanagement.responseVO;

import java.util.ArrayList;

import com.btsl.pretups.network.businesslogic.NetworkVO;

import com.btsl.common.BaseResponse;

public class NetworkListResponseVO extends BaseResponse {
	
	
	public ArrayList<NetworkVO> networkList;

	public ArrayList<NetworkVO> getNetworkList() {
		return networkList;
	}

	public void setNetworkList(ArrayList<NetworkVO> networkList) {
		this.networkList = networkList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NetworkListResponseVO [ networkList := " );
		builder.append(networkList);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}

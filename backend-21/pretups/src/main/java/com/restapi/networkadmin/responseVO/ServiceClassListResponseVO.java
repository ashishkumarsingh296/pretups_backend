package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class ServiceClassListResponseVO extends BaseResponse{

	private ArrayList serviceClassList = null;
	private String networkDescription = null;
	
	
	
	public ArrayList getServiceClassList() {
		return serviceClassList;
	}
	public void setServiceClassList(ArrayList serviceClassList) {
		this.serviceClassList = serviceClassList;
	}
	public String getNetworkDescription() {
		return networkDescription;
	}
	public void setNetworkDescription(String networkDescription) {
		this.networkDescription = networkDescription;
	}
	
	
}

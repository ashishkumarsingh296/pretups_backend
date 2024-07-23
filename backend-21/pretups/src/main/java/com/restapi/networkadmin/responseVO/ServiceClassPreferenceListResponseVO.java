package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class ServiceClassPreferenceListResponseVO extends BaseResponse{

	private ArrayList preferenceList = null;
	private String serviceDescription = null;
	
	
	public ArrayList getPreferenceList() {
		return preferenceList;
	}
	public void setPreferenceList(ArrayList preferenceList) {
		this.preferenceList = preferenceList;
	}
	public String getServiceDescription() {
		return serviceDescription;
	}
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}
	 
	
	
	
}

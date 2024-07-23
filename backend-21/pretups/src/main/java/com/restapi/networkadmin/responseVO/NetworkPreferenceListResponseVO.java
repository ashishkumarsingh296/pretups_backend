package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class NetworkPreferenceListResponseVO extends BaseResponse{
	 private ArrayList _preferenceList = null;
	 private String _module = null;
	 private String _preferenceType = null;
	 private String _networkDescription = null;
	 
	 
	public ArrayList getPreferenceList() {
		return _preferenceList;
	}
	public void setPreferenceList(ArrayList preferenceList) {
		_preferenceList = preferenceList;
	}
	public String getModule() {
		return _module;
	}
	public void setModule(String module) {
		_module = module;
	}
	public String getPreferenceType() {
		return _preferenceType;
	}
	public void setPreferenceType(String preferenceType) {
		_preferenceType = preferenceType;
	}
	public String getNetworkDescription() {
		return _networkDescription;
	}
	public void setNetworkDescription(String networkDescription) {
		_networkDescription = networkDescription;
	}
	 
	 
	 
}

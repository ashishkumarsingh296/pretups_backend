package com.restapi.channelAdmin.serviceMgmt.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class ServiceMgmtInptRespVO extends BaseResponse{
	
	
	private ArrayList domainList;
	private ArrayList serviceDropdownValues;
	
	public ArrayList getDomainList() {
		return domainList;
	}
	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}
	public ArrayList getServiceDropdownValues() {
		return serviceDropdownValues;
	}
	public void setServiceDropdownValues(ArrayList serviceDropdownValues) {
		this.serviceDropdownValues = serviceDropdownValues;
	}
	
	
	
	

}

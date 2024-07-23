package com.restapi.networkadmin.servicePrdMapping.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

public class ServicePrdInputRespVO extends BaseResponse {
	
	private List selectorList;
	private List serviceTypeList;
	public List getSelectorList() {
		return selectorList;
	}
	public void setSelectorList(List selectorList) {
		this.selectorList = selectorList;
	}
	public List getServiceTypeList() {
		return serviceTypeList;
	}
	public void setServiceTypeList(List serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}
	
	
	

}

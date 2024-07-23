package com.restapi.c2s.services;

import java.util.List;

import com.btsl.common.BaseResponse;


public class ReconcileServiceListResponseVO  extends BaseResponse {
	
	List<ServiceListFilter> servicesList;

	@Override
	public String toString() {
		return "ServiceListVO [servicesList=" + servicesList + "]";
	}

	public List<ServiceListFilter> getServicesList() {
		return servicesList;
	}

	public void setServicesList(List<ServiceListFilter> servicesList) {
		this.servicesList = servicesList;
	}

}

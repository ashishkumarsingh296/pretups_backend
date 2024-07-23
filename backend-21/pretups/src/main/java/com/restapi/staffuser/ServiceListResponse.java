package com.restapi.staffuser;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

public class ServiceListResponse extends BaseResponse{
	
	List<ListValueVO> serviceList = new ArrayList<ListValueVO>();

	public List<ListValueVO> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<ListValueVO> serviceList) {
		this.serviceList = serviceList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceListResponse [serviceList=").append(serviceList).append("]");
		return builder.toString();
	}
	
}

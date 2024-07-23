package com.restapi.networkadmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.ListValueVO;

public class ServiceTypeResponseVO extends BaseResponse{
	
	
	private List<ListValueVO> serviceTypeList;

	public List<ListValueVO> getServiceTypeList() {
		return serviceTypeList;
	}

	public void setServiceTypeList(List<ListValueVO> serviceTypeList) {
		this.serviceTypeList = serviceTypeList;
	}
	
	
	
	
	
	
	
	

}

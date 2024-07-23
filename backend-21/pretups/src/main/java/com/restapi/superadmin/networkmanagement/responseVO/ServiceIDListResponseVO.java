package com.restapi.superadmin.networkmanagement.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;


public class ServiceIDListResponseVO extends BaseResponse{
	
	public ArrayList<?> serviceSetID;

	
	public ArrayList<?> getServiceSetID() {
		return serviceSetID;
	}


	public void setServiceSetID(ArrayList<?> serviceSetID) {
		this.serviceSetID = serviceSetID;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NetworkListResponseVO [ networkList := " );
		builder.append(serviceSetID);
		builder.append("]");
		return builder.toString();
	}
}

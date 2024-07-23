package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class DivTypeListResponseVO  extends BaseResponse{
 
	public ArrayList divDepTypeList;

	public ArrayList getDivDepTypeList() {
		return divDepTypeList;
	}

	public void setDivDepTypeList(ArrayList divDepTypeList) {
		this.divDepTypeList = divDepTypeList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DivTypeListResponseVO [divDepTypeList=");
		builder.append(divDepTypeList);
		builder.append("]");
		return builder.toString();
	}
}

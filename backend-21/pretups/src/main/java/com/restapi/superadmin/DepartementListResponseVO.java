package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class DepartementListResponseVO extends BaseResponse {

	public ArrayList departmentList;

	public ArrayList getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(ArrayList departmentList) {
		this.departmentList = departmentList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DepartementListResponseVO [departmentList=");
		builder.append(departmentList);
		builder.append("]");
		return builder.toString();
	}

}

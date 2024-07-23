package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class DivisonListResponseVO extends BaseResponse {

	public ArrayList<DivisionVO> divisionList;

	public ArrayList<DivisionVO> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(ArrayList<DivisionVO> divisionList) {
		this.divisionList = divisionList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DivisonListResponseVO [divisionList=");
		builder.append(divisionList);
		builder.append("]");
		return builder.toString();
	}

}

package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class AssignGeographyResponseVO extends BaseResponse {

	public ArrayList geographyList;

	public ArrayList getGeographyList() {
		return geographyList;
	}

	public void setGeographyList(ArrayList geographyList) {
		this.geographyList = geographyList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssignGeographyResponseVO [geographyList=");
		builder.append(geographyList);
		builder.append("]");
		return builder.toString();
	}

}

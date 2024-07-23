package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class SMSCprofileResponseVO extends BaseResponse {

	public ArrayList smscProfileList;

	public ArrayList getSmscProfileList() {
		return smscProfileList;
	}

	public void setSmscProfileList(ArrayList smscProfileList) {
		this.smscProfileList = smscProfileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMSCprofileResponseVO [smscProfileList=");
		builder.append(smscProfileList);
		builder.append("]");
		return builder.toString();
	}

}

package com.restapi.channelAdmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class DenominationResponse extends BaseResponse {
	public ArrayList<String> mrpList;

	public ArrayList<String> getMrpList() {
		return mrpList;
	}

	public void setMrpList(ArrayList<String> mrpList) {
		this.mrpList = mrpList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DenominationResponse [mrpList=");
		builder.append(mrpList);
		builder.append("]");
		return builder.toString();
	}

}

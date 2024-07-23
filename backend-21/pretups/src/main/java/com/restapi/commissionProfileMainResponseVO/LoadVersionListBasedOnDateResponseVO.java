package com.restapi.commissionProfileMainResponseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class LoadVersionListBasedOnDateResponseVO extends BaseResponse{

	private ArrayList versionList;

	
	
	public ArrayList getVersionList() {
		return versionList;
	}

	public void setVersionList(ArrayList versionList) {
		this.versionList = versionList;
	}
}

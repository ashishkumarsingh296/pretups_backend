package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class PromoLoadParentUserRespVO extends BaseResponse {
	
	private ArrayList userList;

	public ArrayList getUserList() {
		return userList;
	}

	public void setUserList(ArrayList userList) {
		this.userList = userList;
	}
	
}

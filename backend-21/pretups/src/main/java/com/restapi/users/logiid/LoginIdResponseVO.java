package com.restapi.users.logiid;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginIdResponseVO extends BaseResponse {
	@JsonProperty("loginIdExist")
	private boolean loginIdExist;

	private List<String> listLoginIdNew;

	public List<String> getListLoginIdNew() {
		return listLoginIdNew;
	}

	public void setListLoginIdNew(List<String> listLoginIdNew) {
		this.listLoginIdNew = listLoginIdNew;
	}

	public boolean getLoginIdExist() {
		return loginIdExist;
	}

	public void setLoginIdExist(boolean loginIdexist) {
		loginIdExist = loginIdexist;
	}

}

package com.restapi.users.logiid;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordChangeOnLoginVO extends BaseResponse {

	@JsonProperty("isPasswordChange")
	Boolean isPasswordChange;

	public Boolean getIsPasswordChange() {
		return isPasswordChange;
	}

	public void setIsPasswordChange(Boolean isPasswordChange) {
		this.isPasswordChange = isPasswordChange;
	}

	@Override
	public String toString() {
		return "PasswordChangeOnLoginVO [isPasswordChange=" + isPasswordChange + "]";
	}

}

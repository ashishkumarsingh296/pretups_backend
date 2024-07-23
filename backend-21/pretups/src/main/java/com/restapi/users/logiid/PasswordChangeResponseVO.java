package com.restapi.users.logiid;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordChangeResponseVO extends BaseResponse{
	@JsonProperty("ChangePassword") 
	String changePassword;

	public String getChangePassword() {
		return changePassword;
	}

	public void setChangePassword(String changePassword) {
		this.changePassword = changePassword;
	}

	@Override
	public String toString() {
		return "PasswordChangeResponseVO [changePassword=" + changePassword + "]";
	}

	
}
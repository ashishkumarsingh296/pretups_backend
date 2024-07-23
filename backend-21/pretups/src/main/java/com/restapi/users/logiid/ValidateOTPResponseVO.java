package com.restapi.users.logiid;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ValidateOTPResponseVO extends BaseResponse{

	@JsonProperty("loginId")
	private String loginId;
	private boolean disbaleResend;

	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public boolean isDisbaleResend() {
		return disbaleResend;
	}

	public void setDisbaleResend(boolean disbaleResend) {
		this.disbaleResend = disbaleResend;
	}

	@Override
	public String toString() {
		return "ValidateOTPResponseVO [loginId=" + loginId + ", disbaleResend=" + disbaleResend + "]";
	}
	
	
	
}

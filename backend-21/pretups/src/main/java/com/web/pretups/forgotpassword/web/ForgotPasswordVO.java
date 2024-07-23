package com.web.pretups.forgotpassword.web;

import com.btsl.util.BTSLUtil;
import com.fasterxml.jackson.annotation.JsonProperty;



public class ForgotPasswordVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Com@357", required = true)
	@JsonProperty("new password")
	private String newPassword;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Com@357", required = true)
	@JsonProperty("confirm password")
	private String confirmPassword;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUT_02559", required = true)
	@JsonProperty("confirmloginId")
	private String confirmloginId;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getConfirmloginId() {
		return confirmloginId;
	}

	public void setConfirmloginId(String confirmloginId) {
		this.confirmloginId = confirmloginId;
	}

	@Override
	public String toString() {
		return "ForgotPasswordVO [newPassword=" + BTSLUtil.maskParam(newPassword) + ", confirmPassword=" + BTSLUtil.maskParam(confirmPassword)
				+ ", confirmloginId=" + confirmloginId + "]";
	}
}

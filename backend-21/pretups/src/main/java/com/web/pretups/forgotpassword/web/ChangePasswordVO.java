package com.web.pretups.forgotpassword.web;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ChangePasswordVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "Com@357", required = true)
	@JsonProperty("oldpassword")
	private String oldPassword;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Com@357", required = true)
	@JsonProperty("newpassword")
	private String newPassword;

	@io.swagger.v3.oas.annotations.media.Schema(example = "Com@357", required = true)
	@JsonProperty("confirmpassword")
	private String confirmPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

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

	@Override
	public String toString() {
		return "ChangePasswordVO [oldPassword=" + oldPassword + ", newPassword=" + newPassword + ", confirmPassword="
				+ confirmPassword + "]";
	}

}

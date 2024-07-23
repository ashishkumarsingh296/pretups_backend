package com.restapi.users.logiid;

import com.fasterxml.jackson.annotation.JsonProperty;



public class OTPRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "AUT_47998", required = true)
	@JsonProperty("loginId")
	private String loginId;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "EMAIL/SMS", required = true)
	@JsonProperty("mode")
	private String mode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y/N", required = true)
	@JsonProperty("reSend")
	private String reSend;

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

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the reSend
	 */
	public String getReSend() {
		return reSend;
	}

	/**
	 * @param reSend the reSend to set
	 */
	public void setReSend(String reSend) {
		this.reSend = reSend;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OTPRequestVO [loginId=").append(loginId).append(", mode=").append(mode).append(", reSend=")
				.append(reSend).append("]");
		return builder.toString();
	}
	
	
	
}

package com.restapi.users.logiid;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ValidateOTPRequestVO {

	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true)
	@JsonProperty("otp")
	private String otp;

	/**
	 * @return the otp
	 */
	public String getOtp() {
		return otp;
	}

	/**
	 * @param otp the otp to set
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ValidateOTPRequestVO [otp=").append(otp).append("]");
		return builder.toString();
	}
	
	
}

package com.restapi.channeluser.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserTransferOtpRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "70000000", required = true)
	@JsonProperty("msisdn")
	private String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "EMAIL/SMS", required = true)
	@JsonProperty("mode")
	private String mode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y/N", required = true)
	@JsonProperty("reSend")
	private String reSend;

	
	/**
	 * @return the msisdn
	 */
	
	
	public String getMsisdn() {
		return msisdn;
	}
	
	/**
	 * @param msisdn the msisdn to set
	 */

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
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
		builder.append("OTPRequestVO [msisdn=").append(msisdn).append(", mode=").append(mode).append(", reSend=")
				.append(reSend).append("]");
		return builder.toString();
	}
	
	
	
	

}

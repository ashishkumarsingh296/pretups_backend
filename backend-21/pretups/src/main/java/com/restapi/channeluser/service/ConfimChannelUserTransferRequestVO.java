package com.restapi.channeluser.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ConfimChannelUserTransferRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("otp")
	private String otp;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252")
	@JsonProperty("msisdn")
	private String msisdn;

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
	
	
}

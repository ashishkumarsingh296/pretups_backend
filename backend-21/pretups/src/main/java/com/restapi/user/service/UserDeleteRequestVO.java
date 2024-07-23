package com.restapi.user.service;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 
 * @author anshul.goyal2
 *
 */

public class UserDeleteRequestVO {
	
	
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description="External Network Code")
	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	
	@JsonProperty("remarks")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Delete user", required = true, defaultValue = "Remarks")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		return "UserDeleteRequestVO [remarks=" + remarks + ", extnwcode=" + extnwcode + "]";
	}
	

	
}

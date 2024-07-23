package com.restapi.c2s.services;

import com.fasterxml.jackson.annotation.JsonProperty;



public class DvdSwaggVoucherDetails {

	@JsonProperty("voucherType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "digital", required = true/* , defaultValue = "" */, description ="Voucher Type")
	private String voucherType;
	
	@JsonProperty("voucherSegment")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NL", required = true/* , defaultValue = "" */, description ="Voucher segment")
	private String voucherSegment;
	
	@JsonProperty("voucherProfile")
	@io.swagger.v3.oas.annotations.media.Schema(example = "6", required = true/* , defaultValue = "" */, description ="Voucher Profile")
	private String voucherProfile;
	
	@JsonProperty("denomination")
	@io.swagger.v3.oas.annotations.media.Schema(example = "100", required = true/* , defaultValue = "" */, description ="Denomination")
	private String denomination;
	
	@JsonProperty("quantity")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2", required = true/* , defaultValue = "" */, description ="Quantity")
	private String quantity;

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getVoucherSegment() {
		return voucherSegment;
	}

	public void setVoucherSegment(String voucherSegment) {
		this.voucherSegment = voucherSegment;
	}

	public String getVoucherProfile() {
		return voucherProfile;
	}

	public void setVoucherProfile(String voucherProfile) {
		this.voucherProfile = voucherProfile;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "DvdSwaggVoucherDetails [voucherType=" + voucherType + ", voucherSegment=" + voucherSegment
				+ ", voucherProfile=" + voucherProfile + ", denomination=" + denomination + ", quantity=" + quantity
				+ "]";
	}
	
	
	
	


}

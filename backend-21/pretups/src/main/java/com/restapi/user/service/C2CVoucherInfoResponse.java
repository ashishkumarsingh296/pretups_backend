package com.restapi.user.service;

public class C2CVoucherInfoResponse {

	String voucherTypeCode;
	String voucherTypeValue;
	String denomination;
	String segment;
	
	public String getVoucherTypeCode() {
		return voucherTypeCode;
	}
	public void setVoucherTypeCode(String voucherTypeCode) {
		this.voucherTypeCode = voucherTypeCode;
	}
	public String getVoucherTypeValue() {
		return voucherTypeValue;
	}
	public void setVoucherTypeValue(String voucherTypeValue) {
		this.voucherTypeValue = voucherTypeValue;
	}
	
	public String getDenomination() {
		return denomination;
	}
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	
	@Override
	public String toString() {
		return "C2CVoucherInfoResponse [voucherTypecode=" + voucherTypeCode + ", voucherTypeValue=" + voucherTypeValue
				+ ", denomination=" + denomination + ", segment=" + segment + "]";
	}
	

}

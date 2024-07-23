package com.restapi.user.service;

public class C2CVoucherTypeResponse {

	String voucherTypecode;
	String voucherTypeValue;
	
	public String getCode() {
		return voucherTypecode;
	}
	public void setCode(String code) {
		this.voucherTypecode = code;
	}
	public String getValue() {
		return voucherTypeValue;
	}
	public void setValue(String value) {
		this.voucherTypeValue = value;
	}
	
	@Override
	public String toString() {
		return "{key=" + voucherTypecode + ", value=" + voucherTypeValue + "}";
	}
	

}

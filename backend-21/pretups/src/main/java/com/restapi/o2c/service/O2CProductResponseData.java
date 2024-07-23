package com.restapi.o2c.service;

public class O2CProductResponseData {

	String code;
	Long shortCode;
	String name;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Override
	public String toString() {
		return "O2CProductResponseData [code=" + code + ", shortCode=" + shortCode + ", name=" + name + "]";
	}
	public Long getShortCode() {
		return shortCode;
	}
	public void setShortCode(Long shortCode) {
		this.shortCode = shortCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

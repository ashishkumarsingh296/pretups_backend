package com.restapi.o2c.service;

public class ValidateVoucherInfoRequest {
	
	int count;
	
	String fromSerialNumber;
	
	String toSerialNumber;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getFromSerialNumber() {
		return fromSerialNumber;
	}

	public void setFromSerialNumber(String fromSerialNumber) {
		this.fromSerialNumber = fromSerialNumber;
	}

	public String getToSerialNumber() {
		return toSerialNumber;
	}

	public void setToSerialNumber(String toSerialNumber) {
		this.toSerialNumber = toSerialNumber;
	}
	
	

}

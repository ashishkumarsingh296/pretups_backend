package com.btsl.pretups.channel.transfer.businesslogic;

public class VoucherDetails {

	private String fromSerialNum;
	private String toSerialNum;

	public String getFromSerialNum() {
		return fromSerialNum;
	}

	public void setFromSerialNum(String fromSerialNum) {
		this.fromSerialNum = fromSerialNum;
	}

	public String getToSerialNum() {
		return toSerialNum;
	}

	public void setToSerialNum(String toSerialNum) {
		this.toSerialNum = toSerialNum;
	}

	@Override
	public String toString() {
		return "VoucherDetails [fromSerialNum=" + fromSerialNum + ", toSerialNum=" + toSerialNum + "]";
	}

	
}


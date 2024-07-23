package com.restapi.user.service;

import java.util.List;

public class C2CVoucherInfoResponseVO {
	
	String value;
	String displayValue;
	List<VoucherSegmentResponse> segment;
	
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDisplayValue() {
		return displayValue;
	}
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	public List<VoucherSegmentResponse> getSegment() {
		return segment;
	}
	public void setSegment(List<VoucherSegmentResponse> segment) {
		this.segment = segment;
	}
	
	
	
	
	@Override
	public String toString() {
		return "C2CVoucherInfoResponseVO [value=" + value + ", displayValue=" + displayValue + ", segment=" + segment
				+ "]";
	}
}

class VoucherSegmentResponse {

	List<String> denominations;
	
	String segmentType;
	
	String segmentValue;

	public List<String> getDenominations() {
		return denominations;
	}

	public void setDenominations(List<String> denominations) {
		this.denominations = denominations;
	}
	
	public String getSegmentType() {
		return segmentType;
	}

	public void setSegmentType(String segmentType) {
		this.segmentType = segmentType;
	}

	public String getSegmentValue() {
		return segmentValue;
	}

	public void setSegmentValue(String segmentValue) {
		this.segmentValue = segmentValue;
	}

	@Override
	public String toString() {
		return "VoucherSegmentResponse [denominations=" + denominations + ", segmentType=" + segmentType
				+ ", segmentValue=" + segmentValue + "]";
	}


}

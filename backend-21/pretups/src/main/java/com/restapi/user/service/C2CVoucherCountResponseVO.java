package com.restapi.user.service;

import java.util.List;

public class C2CVoucherCountResponseVO {
	
	String voucherType;
	String voucherName;
	List<VoucherSegmentCountResponse> segment;
	
	
	public String getVoucherType() {
		return voucherType;
	}
	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}
	public String getVoucherName() {
		return voucherName;
	}
	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}
	public List<VoucherSegmentCountResponse> getSegment() {
		return segment;
	}
	public void setSegment(List<VoucherSegmentCountResponse> segment) {
		this.segment = segment;
	}
	
	
	
	
	@Override
	public String toString() {
		return "C2CVoucherCountResponseVO [voucherType=" + voucherType + ", voucherName=" + voucherName + ", segment="
				+ segment + "]";
	}
}

class VoucherSegmentCountResponse {

	List<VoucherProfile> voucherDetails;
	
	String segmentType;
	
	String segmentValue;

	public List<VoucherProfile> getVoucherDetails() {
		return voucherDetails;
	}

	public void setVoucherDetails(List<VoucherProfile> denominations) {
		this.voucherDetails = denominations;
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
		return "VoucherSegmentResponse [denominations=" + voucherDetails + ", segmentType=" + segmentType
				+ ", segmentValue=" + segmentValue + "]";
	}


}


class VoucherProfile{
	
	String voucherProfileName;
	
	String denomination;
	
	String noOfVouchersAvailable;

	String voucherProfileID;
	
	String totalAmount;

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	
	public String getVoucherProfileName() {
		return voucherProfileName;
	}

	public void setVoucherProfileName(String voucherProfileName) {
		this.voucherProfileName = voucherProfileName;
	}

	public String getNoOfVouchersAvailable() {
		return noOfVouchersAvailable;
	}

	public void setNoOfVouchersAvailable(String noOfVouchersAvailable) {
		this.noOfVouchersAvailable = noOfVouchersAvailable;
	}
	
	public String getVoucherProfileID() {
		return voucherProfileID;
	}

	public void setVoucherProfileID(String voucherProfileID) {
		this.voucherProfileID = voucherProfileID;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("VoucherProfile [voucherProfileName=").append(voucherProfileName);
		sb.append(", denomination=").append(denomination);
		sb.append(", noOfVouchersAvailable=").append(noOfVouchersAvailable);
		sb.append(", voucherProfileID=").append(voucherProfileID);
		sb.append(", totalAmount=").append(totalAmount).append("]");
		
		return sb.toString();
	}
	
}

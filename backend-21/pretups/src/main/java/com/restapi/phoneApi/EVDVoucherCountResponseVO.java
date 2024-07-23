package com.restapi.phoneApi;

import java.util.List;


public class EVDVoucherCountResponseVO {

	String voucherType;
	String voucherName;
	List<VoucherProfile> voucherDetails;
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
	public List<VoucherProfile> getVoucherDetails() {
		return voucherDetails;
	}
	public void setVoucherDetails(List<VoucherProfile> voucherDetails) {
		this.voucherDetails = voucherDetails;
	}
	
}

class VoucherProfile{
	
	String voucherProfileName;
	
	String denomination;
	
//	String noOfVouchersAvailable;

	String voucherProfileID;
	
//	String totalAmount;

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

//	public String getNoOfVouchersAvailable() {
//		return noOfVouchersAvailable;
//	}
//
//	public void setNoOfVouchersAvailable(String noOfVouchersAvailable) {
//		this.noOfVouchersAvailable = noOfVouchersAvailable;
//	}
	
	public String getVoucherProfileID() {
		return voucherProfileID;
	}

	public void setVoucherProfileID(String voucherProfileID) {
		this.voucherProfileID = voucherProfileID;
	}

//	public String getTotalAmount() {
//		return totalAmount;
//	}
//
//	public void setTotalAmount(String totalAmount) {
//		this.totalAmount = totalAmount;
//	}

	@Override
	public String toString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("VoucherProfile [voucherProfileName=").append(voucherProfileName);
		sb.append(", denomination=").append(denomination);
//		sb.append(", noOfVouchersAvailable=").append(noOfVouchersAvailable);
		sb.append(", voucherProfileID=").append(voucherProfileID);
//		sb.append(", totalAmount=").append(totalAmount).append("]");
		
		return sb.toString();
	}
	
}
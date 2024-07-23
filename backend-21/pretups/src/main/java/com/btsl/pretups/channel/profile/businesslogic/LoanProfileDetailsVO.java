package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;

public class LoanProfileDetailsVO implements Serializable{


	private String profileID;
	private String profileName;
	private String productCode;
	private String categoryCode;
	private String profileType;
	private String networkCode;
	private long fromRange;
	private String fromRangeAsString;
	private long toRange;
	private String toRangeAsString;
	private String interestType;
	private double interestValue;
	private String interestValueAsString;
	private int rowIndex;
	
	public int getRowIndex() {
		return rowIndex;
	}
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	public String getProfileID() {
		return profileID;
	}
	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getProfileType() {
		return profileType;
	}
	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public long getFromRange() {
		return fromRange;
	}
	public void setFromRange(long fromRange) {
		this.fromRange = fromRange;
	}
	public String getFromRangeAsString() {
		return fromRangeAsString;
	}
	public void setFromRangeAsString(String fromRangeAsString) {
		this.fromRangeAsString = fromRangeAsString;
	}
	public long getToRange() {
		return toRange;
	}
	public void setToRange(long toRange) {
		this.toRange = toRange;
	}
	public String getToRangeAsString() {
		return toRangeAsString;
	}
	public void setToRangeAsString(String toRangeAsString) {
		this.toRangeAsString = toRangeAsString;
	}
	public String getInterestType() {
		return interestType;
	}
	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}
	public double getInterestValue() {
		return interestValue;
	}
	public void setInterestValue(double interestValue) {
		this.interestValue = interestValue;
	}
	public String getInterestValueAsString() {
		return interestValueAsString;
	}
	public void setInterestValueAsString(String interestValueAsString) {
		this.interestValueAsString = interestValueAsString;
	}
	

	
	public String toString() {
		final StringBuffer sb = new StringBuffer("LoanProfileDetailsVO Data ");
		sb.append("profileID="+profileID+", ");
		sb.append("productCode="+productCode+", ");
		sb.append("fromRange="+fromRange+", ");
		sb.append("toRange="+toRange+", ");
		sb.append("interestType="+interestType+", ");
		sb.append("interestValue="+interestValue+", ");
		sb.append("profileType="+profileType+", ");
		
		return sb.toString();
	}
	
}

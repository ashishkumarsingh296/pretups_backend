package com.restapi.networkadmin.loanmanagment;
import com.restapi.networkadmin.loanmanagment.LoanProfileDetailsNewVO;

public class ModifyLoanProfileRequestVO {

	
	
	private String profileName;//common
	private String profileType;//common
	private String profileID;//common
	private long fromRange;
	private long toRange;
	private String interestType;
	private double interestValue;
	private String productCode;//common
	private LoanProfileDetailsNewVO loanProfileDetailsList; //LoanProfileDetailsVO
	public LoanProfileDetailsNewVO getLoanProfileDetailsList() {
		return loanProfileDetailsList;
	}
	public void setLoanProfileDetailsList(LoanProfileDetailsNewVO loanProfileDetailsList) {
		this.loanProfileDetailsList = loanProfileDetailsList;
	}
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileType() {
		return profileType;
	}
	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}
	public String getProfileID() {
		return profileID;
	}
	public void setProfileID(String profileID) {
		this.profileID= profileID;
	}
	public long getFromRange() {
		return fromRange;
	}
	public void setFromRange(long fromRange) {
		this.fromRange = fromRange;
	}
	public long getToRange() {
		return toRange;
	}
	public void setToRange(long toRange) {
		this.toRange = toRange;
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
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	
	}
	}

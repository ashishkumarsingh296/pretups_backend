package com.btsl.pretups.channel.profile.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class LoanProfileCombinedVO implements Serializable{

	private String profileID;
	private String profileName;
	private String categoryCode;
	private String profileType;
	private String networkCode;
	private String Status;
	private String productName;
	private String productCode;
	private String createdBy;
	private String modifiedBy;
	private Date createdOn;
	private Date modifiedOn;
	
	private String checkBoxFlag = "N";
	
	
	public String getCheckBoxFlag() {
		return checkBoxFlag;
	}

	public void setCheckBoxFlag(String checkBoxFlag) {
		this.checkBoxFlag = checkBoxFlag;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	
	


	public String getProfileID() {
		return profileID;
	}

	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	private ArrayList loanProfileDetailsList;// it stores list of LoanProfileDetailsVO

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
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

	public ArrayList getLoanProfileDetailsList() {
		return loanProfileDetailsList;
	}

	public void setLoanProfileDetailsList(ArrayList loanProfileDetailsList) {
		this.loanProfileDetailsList = loanProfileDetailsList;
	}
	
	
	public String toString() {
		final StringBuffer sb = new StringBuffer("LoanProfileCombinedVO Data ");
		sb.append("profileID="+profileID+", ");
		sb.append("profileName="+profileName+", ");
		sb.append("profileType="+profileType+", ");
		sb.append("networkCode="+networkCode+", ");
		sb.append("categoryCode="+categoryCode+", ");
		sb.append("Status="+Status+", ");
	
		
		return sb.toString();
	}
	
	
}

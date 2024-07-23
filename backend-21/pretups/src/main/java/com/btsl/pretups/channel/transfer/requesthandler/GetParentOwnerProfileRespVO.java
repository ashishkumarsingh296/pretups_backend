package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.user.businesslogic.UserMsisdnUserIDVO;
/*
 * @(#)FetchUserDetailsResponseVO.java
 * Traveling object for all users details object
 * 
 * @List<UserMsisdnUserIDVO>
 *

 */
public class GetParentOwnerProfileRespVO extends BaseResponseMultiple {
	
	
	private String userName;
	private String status;
	private String grade;
	private String erpCode;
	private String msisdn;
	private String emailID;
	private String address;
	//Parent Info
	private String parentUserID;
	private String parentName;
	private String parentMobileNumber;
	private String parentCategoryName;
	
	//Owner Info
	private String ownerName;
	private String ownerMobileNumber;
	private String ownerCategoryName;
	private String userNamePrefix;
	private String shortName;
	
	public String getParentName() {
		return parentName;
	}


	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	public String getParentMobileNumber() {
		return parentMobileNumber;
	}


	public void setParentMobileNumber(String parentMobileNumber) {
		this.parentMobileNumber = parentMobileNumber;
	}


	public String getParentCategoryName() {
		return parentCategoryName;
	}


	public void setParentCategoryName(String parentCategoryName) {
		this.parentCategoryName = parentCategoryName;
	}


	public String getOwnerName() {
		return ownerName;
	}


	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}


	public String getOwnerMobileNumber() {
		return ownerMobileNumber;
	}


	public void setOwnerMobileNumber(String ownerMobileNumber) {
		this.ownerMobileNumber = ownerMobileNumber;
	}


	public String getOwnerCategoryName() {
		return ownerCategoryName;
	}


	public void setOwnerCategoryName(String ownerCategoryName) {
		this.ownerCategoryName = ownerCategoryName;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getGrade() {
		return grade;
	}


	public void setGrade(String grade) {
		this.grade = grade;
	}


	public String getErpCode() {
		return erpCode;
	}


	public void setErpCode(String erpCode) {
		this.erpCode = erpCode;
	}


	public String getMsisdn() {
		return msisdn;
	}


	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}


	public String getEmailID() {
		return emailID;
	}


	public String getParentUserID() {
		return parentUserID;
	}


	public void setParentUserID(String parentUserID) {
		this.parentUserID = parentUserID;
	}


	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getUserNamePrefix() {
		return userNamePrefix;
	}


	public void setUserNamePrefix(String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}


	public String getShortName() {
		return shortName;
	}


	public void setShortName(String shortName) {
		this.shortName = shortName;
	}


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" GetParentOwnerProfileRespVO : [ ParentName :")
		.append(parentName)
		.append(",Parent Mobile number :")
		.append("parentMobileNumber").append(",Parent Category Name : ").append(parentCategoryName)
		.append(parentCategoryName).append(",ownerName").append(ownerName).append(",Owner mobile number :").append(ownerMobileNumber).append(",OwnerCategoryName :")
		.append(ownerCategoryName)
		.append("]");
		return sb.toString();
	}
	

	
	
}

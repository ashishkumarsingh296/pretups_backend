package com.btsl.user.businesslogic;

import java.io.Serializable;

/**
 * @(#)UserPhoneVO.java Copyright(c) 2005, Bharti Telesoft Ltd. All Rights
 *                      Reserved
 * 
 *                      --------------------------------------------------------
 *                      ----------------------------------------- Author Date
 *                      History
 *                      --------------------------------------------------------
 *                      ----------------------------------------- Mohit Goel
 *                      24/06/2005 Initial Creation
 * 
 *                      This class is used for User Phone Info
 * 
 */
public class UserApprovalVO implements Serializable {

	private String userID;
	private String loginID;
	private String userName;
	private String msisdn;
	private String userStatus;
	private String createdBY;

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getCreatedBY() {
		return createdBY;
	}

	public void setCreatedBY(String createdBY) {
		this.createdBY = createdBY;
	}

	public UserApprovalVO() {
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

}

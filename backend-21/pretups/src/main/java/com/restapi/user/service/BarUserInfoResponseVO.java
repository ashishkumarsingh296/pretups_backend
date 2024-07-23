package com.restapi.user.service;

import com.btsl.common.BaseResponse;

public class BarUserInfoResponseVO extends BaseResponse{

	private boolean senderAllowed;
	private String userName;
	private String msisdn;
	private String category;
	private String domain;
	private String userType;
	public boolean isSenderAllowed() {
		return senderAllowed;
	}
	public void setSenderAllowed(boolean senderAllowed) {
		this.senderAllowed = senderAllowed;
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
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCategory() {
		return category;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getDomain() {
		return domain;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserType() {
		return userType;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BarUserInfoResponseVO [senderAllowed=");
		builder.append(senderAllowed);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", msisdn=");
		builder.append(msisdn);
		builder.append("]");
		return builder.toString();
	}
	
}

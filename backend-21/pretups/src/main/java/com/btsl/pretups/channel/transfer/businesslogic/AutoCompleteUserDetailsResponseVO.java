package com.btsl.pretups.channel.transfer.businesslogic;

public class AutoCompleteUserDetailsResponseVO {
	private String msisdn;
	private String loginId;
	private String userName;
	private String userID;
	
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
	public String getLoginId() {
		return loginId;
	}
	@Override
	public String toString() {
		return "AutoCompleteUserDetailsResponseVO [userName=" + userName + ", msisdn=" + msisdn + ", loginId=" + loginId
				+ "]";
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

}

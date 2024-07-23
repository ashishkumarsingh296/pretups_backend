package com.btsl.pretups.channel.transfer.requesthandler;


public class BarredUserDetailsVO {
	private String userName;
	private String userId;
	private String msisdn;
	private String networkName;
	private String module;
	private String userType;
	private String baredType;
	private String barredOn;
	private String barredBy;
	private String reasonOfBarring;

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getBaredType() {
		return baredType;
	}
	public void setBaredType(String baredType) {
		this.baredType = baredType;
	}
	public String getBarredOn() {
		return barredOn;
	}
	public void setBarredOn(String barredOn) {
		this.barredOn = barredOn;
	}
	public String getBarredBy() {
		return barredBy;
	}
	public void setBarredBy(String barredBy) {
		this.barredBy = barredBy;
	}
	public String getReasonOfBarring() {
		return reasonOfBarring;
	}
	public void setReasonOfBarring(String reasonOfBarring) {
		this.reasonOfBarring = reasonOfBarring;
	}
	@Override
	public String toString() {
		return "BarredUserDetailsVO [userName=" + userName + ", userId=" + userId + ", msisdn=" + msisdn
				+ ", networkName=" + networkName + ", module=" + module + ", userType=" + userType + ", baredType="
				+ baredType + ", barredOn=" + barredOn + ", barredBy=" + barredBy + ", reasonOfBarring="
				+ reasonOfBarring + "]";
	}
	


}

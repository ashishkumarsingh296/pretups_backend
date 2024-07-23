package com.restapi.loggers;

public class LogVO {

	String userID;
	String loginID;
	String userName;
	String msisdn;
	String timeStamp;
	String networkCode;
	String networkName;
	String domainCode;
	String domainName;
	String categoryCode;
	String categoryName;
	String elementCode;
	String componentName;
	String domainType;
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
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
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getElementCode() {
		return elementCode;
	}
	public void setElementCode(String elementCode) {
		this.elementCode = elementCode;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getDomainType() {
		return domainType;
	}
	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LogVO [userID=");
		builder.append(userID);
		builder.append(", loginID=");
		builder.append(loginID);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", msisdn=");
		builder.append(msisdn);
		builder.append(", timeStamp=");
		builder.append(timeStamp);
		builder.append(", networkCode=");
		builder.append(networkCode);
		builder.append(", networkName=");
		builder.append(networkName);
		builder.append(", domainCode=");
		builder.append(domainCode);
		builder.append(", domainName=");
		builder.append(domainName);
		builder.append(", categoryCode=");
		builder.append(categoryCode);
		builder.append(", categoryName=");
		builder.append(categoryName);
		builder.append(", elementCode=");
		builder.append(elementCode);
		builder.append(", componentName=");
		builder.append(componentName);
		builder.append(", domainType=");
		builder.append(domainType);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}

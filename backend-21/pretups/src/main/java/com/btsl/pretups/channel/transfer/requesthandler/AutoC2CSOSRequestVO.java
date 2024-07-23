package com.btsl.pretups.channel.transfer.requesthandler;

public class AutoC2CSOSRequestVO {
	
	private String msisdn ;
	private String loginID ;
	private String userID;
	
	private String  autoc2callowed;
	private String  maxTxnAmount;
	private String sosAllowed ;
    private String sosAllowedAmount ;
    private String sosThresholdLimit ;
	private String lrAllowed ;
	private String lrMaxAmount ;
	private String currentLrAllowedValue ;
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getLoginID() {
		return loginID;
	}
	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getAutoc2callowed() {
		return autoc2callowed;
	}
	public void setAutoc2callowed(String autoc2callowed) {
		this.autoc2callowed = autoc2callowed;
	}
	public String getMaxTxnAmount() {
		return maxTxnAmount;
	}
	public void setMaxTxnAmount(String maxTxnAmount) {
		this.maxTxnAmount = maxTxnAmount;
	}
	public String getSosAllowed() {
		return sosAllowed;
	}
	public void setSosAllowed(String sosAllowed) {
		this.sosAllowed = sosAllowed;
	}
	public String getSosAllowedAmount() {
		return sosAllowedAmount;
	}
	public void setSosAllowedAmount(String sosAllowedAmount) {
		this.sosAllowedAmount = sosAllowedAmount;
	}
	public String getSosThresholdLimit() {
		return sosThresholdLimit;
	}
	public void setSosThresholdLimit(String sosThresholdLimit) {
		this.sosThresholdLimit = sosThresholdLimit;
	}
	public String getLrAllowed() {
		return lrAllowed;
	}
	public void setLrAllowed(String lrAllowed) {
		this.lrAllowed = lrAllowed;
	}
	public String getLrMaxAmount() {
		return lrMaxAmount;
	}
	public void setLrMaxAmount(String lrMaxAmount) {
		this.lrMaxAmount = lrMaxAmount;
	}
	public String getCurrentLrAllowedValue() {
		return currentLrAllowedValue;
	}
	public void setCurrentLrAllowedValue(String currentLrAllowedValue) {
		this.currentLrAllowedValue = currentLrAllowedValue;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AutoC2CSOSRequestVO [msisdn=");
		builder.append(msisdn);
		builder.append(", loginID=");
		builder.append(loginID);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", autoc2callowed=");
		builder.append(autoc2callowed);
		builder.append(", maxTxnAmount=");
		builder.append(maxTxnAmount);
		builder.append(", sosAllowed=");
		builder.append(sosAllowed);
		builder.append(", sosAllowedAmount=");
		builder.append(sosAllowedAmount);
		builder.append(", sosThresholdLimit=");
		builder.append(sosThresholdLimit);
		builder.append(", lrAllowed=");
		builder.append(lrAllowed);
		builder.append(", lrMaxAmount=");
		builder.append(lrMaxAmount);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}

package com.btsl.pretups.user.businesslogic;

import java.io.Serializable;


public class ChannelSoSVO implements Serializable {

	private String userId = null;
	private String msisdn = null;
    private String sosAllowed = null;
    private long sosAllowedAmount = 0;
    private long sosThresholdLimit = 0;
    private String phoneLanguage = null;
    private String country = null;
    private static final long serialVersionUID = 1L;
    public ChannelSoSVO() {
    }
    public ChannelSoSVO(String usrId, String msisdn, String sosAlw, long sosAlwAmt, long sosTL){
    	this.userId = usrId;
    	this.msisdn = msisdn;
    	this.sosAllowed=sosAlw;
    	this.sosAllowedAmount=sosAlwAmt;
    	this.sosThresholdLimit=sosTL;
    }
    public ChannelSoSVO(String usrId, String msisdn, String sosAlw, long sosAlwAmt, long sosTL, String phoneLanguage, String country){
    	this.userId = usrId;
    	this.msisdn = msisdn;
    	this.sosAllowed=sosAlw;
    	this.sosAllowedAmount=sosAlwAmt;
    	this.sosThresholdLimit=sosTL;
    	this.phoneLanguage=phoneLanguage;
    	this.country=country;
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
	public String getSosAllowed() {
		return sosAllowed;
	}
	public void setSosAllowed(String sosAllowed) {
		this.sosAllowed = sosAllowed;
	}
	public long getSosAllowedAmount() {
		return sosAllowedAmount;
	}
	public void setSosAllowedAmount(long sosAllowedAmount) {
		this.sosAllowedAmount = sosAllowedAmount;
	}
	public long getSosThresholdLimit() {
		return sosThresholdLimit;
	}
	public void setSosThresholdLimit(long sosThresholdLimit) {
		this.sosThresholdLimit = sosThresholdLimit;
	}
    public String getPhoneLanguage() {
        return phoneLanguage;
    }
    public void setPhoneLanguage(String language) {
        if (language != null) {
            phoneLanguage = language.trim();
        }
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        if (country != null) {
            country = country.trim();
        }
    }
    public void clearInstance() {
    	this.userId = null;
    	this.msisdn = null;
    	this.sosAllowed = null;
        this.sosAllowedAmount = 0;
        this.sosThresholdLimit = 0;
    	this.phoneLanguage = null;
        this.country = null;

    }
    
    @Override
    public String toString() {
        StringBuilder strBuild = new StringBuilder();
        strBuild.append("userId:").append(userId);
        strBuild.append("msisdn:").append(msisdn);
        strBuild.append("sosAllowed:").append(sosAllowed);
        strBuild.append(" sosAllowedAmount:").append(sosAllowedAmount);
        strBuild.append(" sosThresholdLimit:").append(sosThresholdLimit);
        strBuild.append(" phoneLanguage:").append(phoneLanguage);
        strBuild.append(" country:").append(country);
        return strBuild.toString();
    }
}

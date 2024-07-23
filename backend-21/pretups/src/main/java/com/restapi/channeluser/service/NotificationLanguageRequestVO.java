package com.restapi.channeluser.service;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;



public class NotificationLanguageRequestVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example="ydist", required = true, description = "Login ID of User whose Notification language need to be changed")
	@JsonProperty("userLoginID")
	private String userLoginID;
	
	@io.swagger.v3.oas.annotations.media.Schema(example="", required = true, description = "Phone language details")
	@JsonProperty("changedPhoneLanguageList")
	private ArrayList<ChangePhoneLanguage> changedPhoneLanguageList;

	public String getUserLoginID() {
		return userLoginID;
	}

	public void setUserLoginID(String userLoginID) {
		this.userLoginID = userLoginID;
	}

	public ArrayList<ChangePhoneLanguage> getChangedPhoneLanguageList() {
		return changedPhoneLanguageList;
	}

	public void setChangedPhoneLanguageList(ArrayList<ChangePhoneLanguage> changedPhoneLanguageList) {
		this.changedPhoneLanguageList = changedPhoneLanguageList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NotificationLanguageRequestVO [userLoginID=");
		builder.append(userLoginID);
		builder.append(", changedPhoneLanguageList=");
		builder.append(changedPhoneLanguageList);
		builder.append("]");
		return builder.toString();
	}
	
	
	

}

class ChangePhoneLanguage {
	
	@io.swagger.v3.oas.annotations.media.Schema(example="72525252", required = true, description = "User MSISDN")
	@JsonProperty("userMsisdn")
	private String userMsisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example="ar", required = true, description = "Updated language code")
	@JsonProperty("languageCode")
	private String languageCode;
	
	@io.swagger.v3.oas.annotations.media.Schema(example="NG", required = true, description = "Updated language country")
	@JsonProperty("country")
	private String country;

	public String getUserMsisdn() {
		return userMsisdn;
	}

	public void setUserMsisdn(String userMsisdn) {
		this.userMsisdn = userMsisdn;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChangePhoneLanguage [userMsisdn=");
		builder.append(userMsisdn);
		builder.append(", languageCode=");
		builder.append(languageCode);
		builder.append(", country=");
		builder.append(country);
		builder.append("]");
		return builder.toString();
	}
	
	
}

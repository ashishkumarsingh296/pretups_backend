package com.restapi.channeluser.service;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class NotificationLanguageResponseVO extends BaseResponse{

	private String msisdn;
	private String loginID;
	private String userName;
	private String userID;
	
	private ArrayList userList;
	private int userListSize = 0;
	
	private ArrayList languageList;
	
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public ArrayList getUserList() {
		return userList;
	}
	public void setUserList(ArrayList userList) {
		this.userList = userList;
	}
	
	public int getUserListSize() {
		return userListSize;
	}
	public void setUserListSize(int userListSize) {
		this.userListSize = userListSize;
	}
	
	public ArrayList getLanguageList() {
		return languageList;
	}
	public void setLanguageList(ArrayList languageList) {
		this.languageList = languageList;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NotificationLanguageResponseVO [msisdn=");
		builder.append(msisdn);
		builder.append(", loginID=");
		builder.append(loginID);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", userID=");
		builder.append(userID);
		builder.append(", userList=");
		builder.append(userList);
		builder.append(", userListSize=");
		builder.append(userListSize);
		builder.append(", languageList=");
		builder.append(languageList);
		builder.append("]");
		return builder.toString();
	}
	
	
}

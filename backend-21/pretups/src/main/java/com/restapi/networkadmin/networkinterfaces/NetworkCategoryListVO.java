package com.restapi.networkadmin.networkinterfaces;

import java.util.ArrayList;

public class NetworkCategoryListVO {

	private int status;
	private String message;
	private String messageCode;
	private ArrayList categoryList;
	private ArrayList interfaceNameList;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public ArrayList getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	public ArrayList getInterfaceNameList() {
		return interfaceNameList;
	}
	public void setInterfaceNameList(ArrayList interfaceNameList) {
		this.interfaceNameList = interfaceNameList;
	}
	
	
}

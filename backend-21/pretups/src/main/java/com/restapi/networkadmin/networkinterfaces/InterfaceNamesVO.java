package com.restapi.networkadmin.networkinterfaces;

import java.util.ArrayList;

public class InterfaceNamesVO {

	private int status;
	private String message;
	private String messageCode;
	private ArrayList interfaceNamesList;
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
	public ArrayList getInterfaceNamesList() {
		return interfaceNamesList;
	}
	public void setInterfaceNamesList(ArrayList interfaceNamesList) {
		this.interfaceNamesList = interfaceNamesList;
	}
	
	
}

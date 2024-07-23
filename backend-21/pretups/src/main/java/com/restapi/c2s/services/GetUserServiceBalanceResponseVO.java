package com.restapi.c2s.services;

import java.util.ArrayList;



public class GetUserServiceBalanceResponseVO {
     private String messageCode;
	 private String message;
	 private String status;
	 public String getMessageCode() {
		return messageCode;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GetUserServiceBalanceResponseVO [messageCode=");
		builder.append(messageCode);
		builder.append(", message=");
		builder.append(message);
		builder.append(", status=");
		builder.append(status);
		builder.append(", serviceList=");
		builder.append(serviceList);
		builder.append("]");
		builder.append(", balanceList=");
		builder.append(list);
		builder.append("]");
		return builder.toString();
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<ServiceViceBalanceVO> getServiceList() {
		return serviceList;
	}
	public ArrayList<UserBalanceVO> getList() {
		return list;
	}
	public void setList(ArrayList<UserBalanceVO> list) {
		this.list = list;
	}
	public void setServiceList(ArrayList<ServiceViceBalanceVO> serviceList) {
		this.serviceList = serviceList;
	}
	private ArrayList<ServiceViceBalanceVO> serviceList ;
	private ArrayList<UserBalanceVO> list;
	 
}

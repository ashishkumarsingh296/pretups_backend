package com.restapi.networkadmin.networkinterfaces;

import java.util.ArrayList;

import com.btsl.pretups.interfaces.businesslogic.InterfaceNetworkMappingVO;

public class NetworkInterfacesVO {

	private int status;
	private String message;
	private String messageCode;
	private ArrayList<InterfaceNetworkMappingVO> interfaceList;
	
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
	public ArrayList<InterfaceNetworkMappingVO> getInterfaceList() {
		return interfaceList;
	}
	public void setInterfaceList(ArrayList<InterfaceNetworkMappingVO> interfaceList) {
		this.interfaceList = interfaceList;
	}
	
	
	
}

package com.restapi.channelAdmin.serviceMgmt.responseVO;

import java.util.ArrayList;

public class ServiceManagementUIDataTableVO {
	
	private String domainCode;
	private String domainName;
	private String categoryCode;
	private String categoryName;
	
	private ArrayList<C2Sservices> listOfC2SServices;
	private ArrayList<OtherServices> listofOtherServices;
	
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public ArrayList<C2Sservices> getListOfC2SServices() {
		return listOfC2SServices;
	}
	public void setListOfC2SServices(ArrayList<C2Sservices> listOfC2SServices) {
		this.listOfC2SServices = listOfC2SServices;
	}
	public ArrayList<OtherServices> getListofOtherServices() {
		return listofOtherServices;
	}
	public void setListofOtherServices(ArrayList<OtherServices> listofOtherServices) {
		this.listofOtherServices = listofOtherServices;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	
	
	
	
	
	

}

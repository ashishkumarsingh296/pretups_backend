package com.restapi.c2s.services;

public class ServiceViceBalanceVO {

	
	private String  serviceCode;
	private String serviceName;
	private String balanceAssociated;
	
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getBalanceAssociated() {
		return balanceAssociated;
	}
	public void setBalanceAssociated(String balanceAssociated) {
		this.balanceAssociated = balanceAssociated;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceViceBalanceVO [serviceCode=");
		builder.append(serviceCode);
		builder.append(", serviceName=");
		builder.append(serviceName);
		builder.append(", balanceAssociated=");
		builder.append(balanceAssociated);
		builder.append("]");
		return builder.toString();
	}
	

}

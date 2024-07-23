package com.restapi.c2s.services;

public class ServiceListFilter {

	String name;
	String status;
	String serviceType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ServiceListFilter [name=" + name + ", status=" + status + ", serviceType=" + serviceType + "]";
	}
	
}

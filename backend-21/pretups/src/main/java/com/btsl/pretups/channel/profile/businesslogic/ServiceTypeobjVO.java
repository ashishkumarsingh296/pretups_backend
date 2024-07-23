package com.btsl.pretups.channel.profile.businesslogic;

/*
 * 
 * 
 * ServicekeywordobjVO
 *

 */
public class ServiceTypeobjVO {

	// Commission Slabs details

	private String serviceType;
	private String serviceTypeName;
	private String module;
	private String request_param;
	private String subKeyWordApplicable;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getServiceTypeName() {
		return serviceTypeName;
	}

	public void setServiceTypeName(String serviceTypeName) {
		this.serviceTypeName = serviceTypeName;
	}

	public String getRequest_param() {
		return request_param;
	}

	public void setRequest_param(String request_param) {
		this.request_param = request_param;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" ServicekeywordobjVO : [ serviceType :");
		sb.append(serviceType);
		sb.append(", module :");
		sb.append(module);
		sb.append("]");
		return sb.toString();
	}

	public String getSubKeyWordApplicable() {
		return subKeyWordApplicable;
	}

	public void setSubKeyWordApplicable(String subKeyWordApplicable) {
		this.subKeyWordApplicable = subKeyWordApplicable;
	}

}

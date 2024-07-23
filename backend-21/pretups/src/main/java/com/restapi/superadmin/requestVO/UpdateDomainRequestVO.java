package com.restapi.superadmin.requestVO;

public class UpdateDomainRequestVO {

	private String domainCodeforDomain;
	private String domainName;
	private String numberOfCategories;
	private String domainStatus;
	private long lastModifiedTime;
	
	public String getDomainCodeforDomain() {
		return domainCodeforDomain;
	}
	public void setDomainCodeforDomain(String domainCodeforDomain) {
		this.domainCodeforDomain = domainCodeforDomain;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getNumberOfCategories() {
		return numberOfCategories;
	}
	public void setNumberOfCategories(String numberOfCategories) {
		this.numberOfCategories = numberOfCategories;
	}

	public String getDomainStatus() {
		return domainStatus;
	}
	public void setDomainStatus(String domainStatus) {
		this.domainStatus = domainStatus;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	
	
}

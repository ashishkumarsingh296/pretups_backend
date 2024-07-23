package com.restapi.superadmin.requestVO;

import com.btsl.pretups.domain.businesslogic.DomainVO;

public class DeleteDomainRequestVO {

	private String domainCodeforDomain;
   
    private long lastModifiedTime;
    
    private Boolean isSuspend;
    
	public String getDomainCodeforDomain() {
		return domainCodeforDomain;
	}
	public void setDomainCodeforDomain(String domainCodeforDomain) {
		this.domainCodeforDomain = domainCodeforDomain;
	}
	
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public Boolean getIsSuspend() {
		return isSuspend;
	}
	public void setIsSuspend(Boolean isSuspend) {
		this.isSuspend = isSuspend;
	}

	
	@Override
	public String toString() {
		return "DeleteDomainRequestVO [domainCodeforDomain=" + domainCodeforDomain
				+ ", lastModifiedTime=" + lastModifiedTime + ", isSuspend=" + isSuspend + "]";
	}
	public static DomainVO convertObject(DeleteDomainRequestVO request)
	{
		DomainVO object = new DomainVO();
		object.setDomainCodeforDomain(request.getDomainCodeforDomain());
		object.setLastModifiedTime(request.getLastModifiedTime());
		return object;
	}
	
}

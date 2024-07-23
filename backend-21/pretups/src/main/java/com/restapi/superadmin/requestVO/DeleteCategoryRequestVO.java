package com.restapi.superadmin.requestVO;

import com.btsl.pretups.domain.businesslogic.CategoryVO;

public class DeleteCategoryRequestVO {

	private String categoryCode;
	
	private String categoryName;
	
	private long lastModifiedTime;
	
	private String fixedRoles;
	
	private String domainCode;
	
	private int sequenceNumber;
	
	private String agentAllowed;

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getFixedRoles() {
		return fixedRoles;
	}

	public void setFixedRoles(String fixedRoles) {
		this.fixedRoles = fixedRoles;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getAgentAllowed() {
		return agentAllowed;
	}

	public void setAgentAllowed(String agentAllowed) {
		this.agentAllowed = agentAllowed;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public static CategoryVO setCategoryObj(DeleteCategoryRequestVO request)
	{
		CategoryVO categoryVo = new CategoryVO();
		categoryVo.setCategoryCode(request.getCategoryCode());
		categoryVo.setCategoryName(request.getCategoryName());
		categoryVo.setLastModifiedTime(request.getLastModifiedTime());
		categoryVo.setFixedRoles(request.getFixedRoles());
		categoryVo.setDomainCodeforCategory(request.getDomainCode());
		categoryVo.setSequenceNumber(request.getSequenceNumber());
		categoryVo.setAgentAllowed(request.getAgentAllowed());
		return categoryVo;
	}
}

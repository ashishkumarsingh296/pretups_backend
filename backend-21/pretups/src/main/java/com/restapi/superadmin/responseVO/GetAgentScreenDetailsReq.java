package com.restapi.superadmin.responseVO;

public class GetAgentScreenDetailsReq {
	
	private String domainCode;
	private String parentCategoryCode;
	private String categoryGeoDomainType;
	
	
	public  GetAgentScreenDetailsReq(){
		
	}
	
	public GetAgentScreenDetailsReq(String domainCode, String parentCategoryCode, String categoryGeoDomainType) {
		super();
		this.domainCode = domainCode;
		this.parentCategoryCode = parentCategoryCode;
		this.categoryGeoDomainType = categoryGeoDomainType;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getParentCategoryCode() {
		return parentCategoryCode;
	}
	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}
	
	
	
	public String getCategoryGeoDomainType() {
		return categoryGeoDomainType;
	}
	public void setCategoryGeoDomainType(String categoryGeoDomainType) {
		this.categoryGeoDomainType = categoryGeoDomainType;
	}
	@Override
	public Object clone() {
		GetAgentScreenDetailsReq getAgentScreenDetailsReq = null;
	    try {
	    	getAgentScreenDetailsReq = (GetAgentScreenDetailsReq) super.clone();
	    } catch (CloneNotSupportedException e) {
	    	getAgentScreenDetailsReq = new GetAgentScreenDetailsReq(
	          this.getDomainCode(), this.getParentCategoryCode(),this.categoryGeoDomainType);
	    }
	   
	    return getAgentScreenDetailsReq;
	}

}

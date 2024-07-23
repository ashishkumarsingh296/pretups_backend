package com.restapi.staffuser;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchAdminInputParams {
	
	@JsonProperty("domainCode")
	private String domainCode;

	@JsonProperty("categoryCode")
	private String categoryCode;
	
	@JsonProperty("parentCategory")
	private String parentCategory;
	
	@JsonProperty("geography")
	private String geography;
	
	@JsonProperty("ownerUser")
	private String ownerUser;
	
	@JsonProperty("parentUser")
	private String parentUser;
	
	@JsonProperty("channelUser")
	private String channelUser;
	

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

	public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(String ownerUser) {
		this.ownerUser = ownerUser;
	}

	public String getParentUser() {
		return parentUser;
	}

	public void setParentUser(String parentUser) {
		this.parentUser = parentUser;
	}

	public String getChannelUser() {
		return channelUser;
	}

	public void setChannelUser(String channelUser) {
		this.channelUser = channelUser;
	}
	
	
	
	
	

}

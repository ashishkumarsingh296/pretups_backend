package com.restapi.channeluser.service;


import com.fasterxml.jackson.annotation.JsonProperty;



public class UserHierarchyRequestVO {
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	@JsonProperty("userDomain")
	private String userDomain;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true)
	@JsonProperty("parentCategory")
	private String parentCategory;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "HARYANA", required = true)
	@JsonProperty("geography")
	private String geography;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "SE", required = true)
	@JsonProperty("userCategory")
	private String userCategory;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true)
	@JsonProperty("status")
	private String status;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true)
	@JsonProperty("msisdn")
	private String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required = true)
	@JsonProperty("loginId")
	private String loginId;

	/**
	 * @return the userDomain
	 */
	public String getUserDomain() {
		return userDomain;
	}

	/**
	 * @param userDomain the userDomain to set
	 */
	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}

	/**
	 * @return the parentCategory
	 */
	public String getParentCategory() {
		return parentCategory;
	}

	/**
	 * @param parentCategory the parentCategory to set
	 */
	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	/**
	 * @return the geography
	 */
	public String getGeography() {
		return geography;
	}

	/**
	 * @param geography the geography to set
	 */
	public void setGeography(String geography) {
		this.geography = geography;
	}

	/**
	 * @return the userCategory
	 */
	public String getUserCategory() {
		return userCategory;
	}

	/**
	 * @param userCategory the userCategory to set
	 */
	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the msisdn
	 */
	public String getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserHierarchyRequestVO [userDomain=").append(userDomain).append(", parentCategory=")
				.append(parentCategory).append(", geography=").append(geography).append(", userCategory=")
				.append(userCategory).append(", status=").append(status).append(", msisdn=").append(msisdn)
				.append(", loginId=").append(loginId).append("]");
		return builder.toString();
	}
	 
	 
	 

}

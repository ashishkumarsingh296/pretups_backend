package com.restapi.channeluser.service;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserTransferRequestVO {
	

	@io.swagger.v3.oas.annotations.media.Schema(example = "7252525")
	@JsonProperty("msisdn")
	private String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "HARYANA")
	@JsonProperty("domain")
	private String domain;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST")
	@JsonProperty("category")
	private String category;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "GURGAON")
	@JsonProperty("geography")
	private String geography;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ydist")
	@JsonProperty("ownerId")
	private String ownerId;

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
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
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
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChannelUserTransferRequestVO [msisdn=").append(msisdn).append(", domain=").append(domain)
				.append(", category=").append(category).append(", geography=").append(geography).append(", ownerId=")
				.append(ownerId).append("]");
		return builder.toString();
	}
	
	
}

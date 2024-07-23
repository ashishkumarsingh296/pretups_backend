package com.restapi.channeluser.service;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.transfer.requesthandler.FetchUserDetailsResponseVO;
import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserTransferResponseVO extends BaseResponse{

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
	@JsonProperty("parentName")
	private String parentName;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "root")
	@JsonProperty("userName")
	private String userName;

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
	 * @return the parentName
	 */
	public String getParentName() {
		return parentName;
	}

	/**
	 * @param parentName the parentName to set
	 */
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChannelUserTransferResponseVO [domain=").append(domain).append(", category=").append(category)
				.append(", geography=").append(geography).append(", parentName=").append(parentName)
				.append(", userName=").append(userName).append("]");
		return builder.toString();
	}
	
	
	
}

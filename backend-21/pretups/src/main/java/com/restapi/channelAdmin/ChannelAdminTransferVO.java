package com.restapi.channelAdmin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelAdminTransferVO {

	
	
	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("fromParentUserId")
	private String fromParentUserId;
	
	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("toParentUserId")
	private String toParentUserId;

	/**
	 * @return the userId
	 */
	@JsonIgnoreProperties
	@JsonIgnore
	public String getUserId() {
		return fromParentUserId;
	}

	/**
	 * @param userId the userId to set
	 */
	@JsonIgnoreProperties
	@JsonIgnore
	public void setUserId(String userId) {
		this.fromParentUserId = userId;
	}

	/**
	 * @return the toParentUser
	 */
	@JsonIgnoreProperties
	@JsonIgnore
	public String getToParentUser() {
		return toParentUserId;
	}

	/**
	 * @param toParentUser the toParentUser to set
	 */
	@JsonIgnoreProperties
	@JsonIgnore
	public void setToParentUser(String toParentUser) {
		this.toParentUserId = toParentUser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChannelAdminTransferVO [fromParentUserId=").append(fromParentUserId).append(", toParentUserId=").append(toParentUserId)
				.append("]");
		return builder.toString();
	}

	
	
}

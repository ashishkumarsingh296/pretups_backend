package com.restapi.channelAdmin;

import com.fasterxml.jackson.annotation.JsonProperty;



public class ChannelUserListByParntReqVO {



	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("domain")
	private String domain;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("userCategory")
	private String userCategory;
	
	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("ownerUserID")
	private String ownerUserID;


	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("parentUserID")
	private String parentUserID;

	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("geography")
	private String geography;

	
	
	@io.swagger.v3.oas.annotations.media.Schema()
	@JsonProperty("userName")
	private String userName;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getParentUserID() {
		return parentUserID;
	}

	public void setParentUserID(String parentUserID) {
		this.parentUserID = parentUserID;
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}



	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ChannelUserListRequestVO [");
		stringBuilder.append(", domain=");
		stringBuilder.append(domain);
		stringBuilder.append(", userCategory=");
		stringBuilder.append(userCategory);
		stringBuilder.append(", parentUserID=");
		stringBuilder.append(parentUserID);
		stringBuilder.append(", geography=");
		stringBuilder.append(geography);
		stringBuilder.append(", status=");
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOwnerUserID() {
		return ownerUserID;
	}

	public void setOwnerUserID(String ownerUserID) {
		this.ownerUserID = ownerUserID;
	}

}

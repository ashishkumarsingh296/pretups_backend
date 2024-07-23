package com.restapi.channelAdmin.requestVO;



public class OwnerListAndCUListO2cTxnRevRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String geography;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String domain;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String category;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String ownerUsername;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String ownerUserId;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
	private String channelUserUsername;

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	public String getOwnerUserId() {
		return ownerUserId;
	}

	public void setOwnerUserId(String ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public String getChannelUserUsername() {
		return channelUserUsername;
	}

	public void setChannelUserUsername(String channelUserUsername) {
		this.channelUserUsername = channelUserUsername;
	}
	
	
	
	
}

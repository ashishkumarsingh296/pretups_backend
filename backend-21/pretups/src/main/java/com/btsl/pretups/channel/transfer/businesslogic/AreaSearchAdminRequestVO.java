package com.btsl.pretups.channel.transfer.businesslogic;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;



public class AreaSearchAdminRequestVO extends BaseResponse{

	@JsonProperty("domainCode")
	private String domainCode;
	
	@JsonProperty("categoryCode")
	private String categoryCode;
	
	@JsonProperty("geoDomainCode")
	private String geoDomainCode;
	
	@JsonProperty("parentLoginId")
	private String parentLoginId;
	
	@JsonProperty("ownerLoginId")
	private String ownerLoginId;
	
	@JsonProperty("searchLoginId")
	private String searchLoginId;

	@JsonProperty("requestType")
	private String requestType;
	
	public AreaSearchAdminRequestVO(String domainCode, String categoryCode, String geoDomainCode,
			String parentLoginId, String requestType) {
		super();
		this.domainCode = domainCode;
		this.categoryCode = categoryCode;
		this.geoDomainCode = geoDomainCode;
		this.parentLoginId = parentLoginId;
		this.requestType = requestType;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "RET", required = true/* , defaultValue = "" */)
	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	@io.swagger.v3.oas.annotations.media.Schema(example = "BG", required = true/* , defaultValue = "" */)
	public String getGeoDomainCode() {
		return geoDomainCode;
	}
	
	public void setGeoDomainCode(String geoDomainCode) {
		this.geoDomainCode = geoDomainCode;
	}

	
	@io.swagger.v3.oas.annotations.media.Schema(example = "agent007", required = true/* , defaultValue = "" */)
	public String getParentLoginId() {
		return parentLoginId;
	}

	public void setParentLoginId(String parentLoginId) {
		this.parentLoginId = parentLoginId;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */)
	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "agent007", required = true/* , defaultValue = "" */)
	public String getOwnerLoginId() {
		return ownerLoginId;
	}

	public void setOwnerLoginId(String ownerLoginId) {
		this.ownerLoginId = ownerLoginId;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "agent007", required = true/* , defaultValue = "" */)
	public String getSearchLoginId() {
		return searchLoginId;
	}

	public void setSearchLoginId(String searchLoginId) {
		this.searchLoginId = searchLoginId;
	}
	
}

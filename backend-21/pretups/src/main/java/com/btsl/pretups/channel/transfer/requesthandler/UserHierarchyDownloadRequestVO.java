package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restapi.user.service.UserHierarchyUIResponseData;

public class UserHierarchyDownloadRequestVO {

	@JsonProperty("loginID")
	String loginID;

	@JsonProperty("msisdn")
	String msisdn;

	@JsonProperty("ownerUserId")
	String ownerName;

	@JsonProperty("parentCategory")
	String parentCategory;

	@JsonProperty("parentUserId")
	String parentUserId;

	@JsonProperty("userCategory")
	String userCategory;

	@JsonProperty("userStatus")
	String userStatus;
	
	@JsonProperty("zoneDesc")
	String zoneDesc;
	
	@JsonProperty("domainCode")
	String domainCode;
	
	@JsonProperty("fileType")
	String fileType = "xls";
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getDomainCode() {
		return domainCode;
	}

	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}


	public String getZoneDesc() {
		return zoneDesc;
	}

	public void setZoneDesc(String zoneDesc) {
		this.zoneDesc = zoneDesc;
	}

	@JsonProperty("advancedSearch")
	boolean advancedSearch;

	public String getParentUserId() {
		return parentUserId;
	}

	public void setParentUserId(String parentUserId) {
		this.parentUserId = parentUserId;
	}

	public boolean isAdvancedSearch() {
		return advancedSearch;
	}

	public void setAdvancedSearch(boolean advancedSearch) {
		this.advancedSearch = advancedSearch;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}

	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

}

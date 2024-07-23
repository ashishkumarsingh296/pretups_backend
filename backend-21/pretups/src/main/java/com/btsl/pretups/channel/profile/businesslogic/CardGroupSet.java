package com.btsl.pretups.channel.profile.businesslogic;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;



public class CardGroupSet {
	
	@JsonProperty("cardGroupSetName")
	String cardGroupSetName;
	
	@JsonProperty("serviceTypeDesc")
	String serviceTypeDesc;
	
	@JsonProperty("subServiceTypeDescription")
	String subServiceTypeDescription;
	
	@JsonProperty("modifiedBy")
	String modifiedBy;
	
	@JsonProperty("language1Message")
	String language1Message;
	
	@JsonProperty("language2Message")
	String language2Message;

	
	@JsonProperty("version")
    String version;
	
	@JsonProperty("applicableFromDate")
    String applicableFromDate;
	
	@JsonProperty("applicableFromHour")
    String applicableFromHour;
    
	@JsonProperty("oldApplicableFromDate")
	String oldApplicableFromDate;
	
	@JsonProperty("oldApplicableFromHour")
    String oldApplicableFromHour;
	
	@JsonProperty("cardGroupID")
    String cardGroupID;
	
	@JsonProperty("amountTypeList")
    String amountTypeList;
	
	@JsonProperty("validityTypeList")
    String validityTypeList;
	
	@JsonProperty("subServiceTypeList")
    String subServiceTypeList;
	
	@JsonProperty("setTypeList")
    String setTypeList;
	
	@JsonProperty("cardGroupSetNameList")
    String cardGroupSetNameList;
	
	@JsonProperty("cardGroupSetVersionList")
    String cardGroupSetVersionList;
	
	@JsonProperty("cardGroupSetID")
    String cardGroupSetID;
	
	@JsonProperty("serviceType")
    String serviceType;
	
	@JsonProperty("networkCode")
    String networkCode;
	
	@JsonProperty("createdBy")
    String createdBy;
	
	@JsonProperty("createdOn")
    String createdOn;
	
	@JsonProperty("modifiedOn")
    Date modifiedOn;
	
	@JsonProperty("moduleCode")
    String moduleCode;
	
	@JsonProperty("setType")
    String setType;
	
	@JsonProperty("setTypeName")
    String setTypeName;
	
	@JsonProperty("previousDefaultCardGroup")
    String previousDefaultCardGroup;
	
	@JsonProperty("defaultCardGroup")
    String defaultCardGroup;
	
	@JsonProperty("lastVersion")
    String lastVersion;
	
	@JsonProperty("lastModifiedOn")
    String lastModifiedOn;
	
	@JsonProperty("subServiceType")
    String subServiceType;
	
	@JsonProperty("status")
    String status;
    		
	@JsonProperty("version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@JsonProperty("applicableFromDate")
	public String getApplicableFromDate() {
		return applicableFromDate;
	}

	public void setApplicableFromDate(String applicableFromDate) {
		this.applicableFromDate = applicableFromDate;
	}

	@JsonProperty("applicableFromHour")
	public String getApplicableFromHour() {
		return applicableFromHour;
	}

	public void setApplicableFromHour(String applicableFromHour) {
		this.applicableFromHour = applicableFromHour;
	}

	@JsonProperty("oldApplicableFromDate")
	public String getOldApplicableFromDate() {
		return oldApplicableFromDate;
	}

	public void setOldApplicableFromDate(String oldApplicableFromDate) {
		this.oldApplicableFromDate = oldApplicableFromDate;
	}

	@JsonProperty("oldApplicableFromHour")
	public String getOldApplicableFromHour() {
		return oldApplicableFromHour;
	}

	public void setOldApplicableFromHour(String oldApplicableFromHour) {
		this.oldApplicableFromHour = oldApplicableFromHour;
	}

	@JsonProperty("cardGroupID")
	public String getCardGroupID() {
		return cardGroupID;
	}

	public void setCardGroupID(String cardGroupID) {
		this.cardGroupID = cardGroupID;
	}

	@JsonProperty("amountTypeList")
	public String getAmountTypeList() {
		return amountTypeList;
	}

	public void setAmountTypeList(String amountTypeList) {
		this.amountTypeList = amountTypeList;
	}

	@JsonProperty("validityTypeList")
	public String getValidityTypeList() {
		return validityTypeList;
	}

	public void setValidityTypeList(String validityTypeList) {
		this.validityTypeList = validityTypeList;
	}

	@JsonProperty("subServiceTypeList")
	public String getSubServiceTypeList() {
		return subServiceTypeList;
	}

	public void setSubServiceTypeList(String subServiceTypeList) {
		this.subServiceTypeList = subServiceTypeList;
	}

	@JsonProperty("setTypeList")
	public String getSetTypeList() {
		return setTypeList;
	}

	public void setSetTypeList(String setTypeList) {
		this.setTypeList = setTypeList;
	}

	@JsonProperty("cardGroupSetNameList")
	public String getCardGroupSetNameList() {
		return cardGroupSetNameList;
	}

	public void setCardGroupSetNameList(String cardGroupSetNameList) {
		this.cardGroupSetNameList = cardGroupSetNameList;
	}

	@JsonProperty("cardGroupSetVersionList")
	public String getCardGroupSetVersionList() {
		return cardGroupSetVersionList;
	}

	public void setCardGroupSetVersionList(String cardGroupSetVersionList) {
		this.cardGroupSetVersionList = cardGroupSetVersionList;
	}

	@JsonProperty("cardGroupSetID")
	public String getCardGroupSetID() {
		return cardGroupSetID;
	}

	public void setCardGroupSetID(String cardGroupSetID) {
		this.cardGroupSetID = cardGroupSetID;
	}

	@JsonProperty("serviceType")
	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@JsonProperty("networkCode")
	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	@JsonProperty("createdBy")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@JsonProperty("createdOn")
	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	@JsonProperty("modifiedOn")
	public Date getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	@JsonProperty("moduleCode")
	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	@JsonProperty("setType")
	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	@JsonProperty("setTypeName")
	public String getSetTypeName() {
		return setTypeName;
	}

	public void setSetTypeName(String setTypeName) {
		this.setTypeName = setTypeName;
	}

	@JsonProperty("previousDefaultCardGroup")
	public String getPreviousDefaultCardGroup() {
		return previousDefaultCardGroup;
	}

	public void setPreviousDefaultCardGroup(String previousDefaultCardGroup) {
		this.previousDefaultCardGroup = previousDefaultCardGroup;
	}

	@JsonProperty("defaultCardGroup")
	public String getDefaultCardGroup() {
		return defaultCardGroup;
	}

	public void setDefaultCardGroup(String defaultCardGroup) {
		this.defaultCardGroup = defaultCardGroup;
	}

	@JsonProperty("lastVersion")
	public String getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}

	@JsonProperty("lastModifiedOn")
	public String getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(String lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	@JsonProperty("subServiceType")
	public String getSubServiceType() {
		return subServiceType;
	}
	

	public void setSubServiceType(String subServiceType) {
		this.subServiceType = subServiceType;
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	@JsonProperty("cardGroupSetName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "AUTKc1Al9", required = true/* , defaultValue = "" */)
	public String getCardGroupSetName() {
		return cardGroupSetName;
	}

	public void setCardGroupSetName(String cardGroupSetName) {
		this.cardGroupSetName = cardGroupSetName;
	}

	@JsonProperty("serviceTypeDesc")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Voucher Consumption", required = true/* , defaultValue = "" */)
	public String getServiceTypeDesc() {
		return serviceTypeDesc;
	}

	public void setServiceTypeDesc(String serviceTypeDesc) {
		this.serviceTypeDesc = serviceTypeDesc;
	}

	@JsonProperty("subServiceTypeDescription")
	@io.swagger.v3.oas.annotations.media.Schema(example = "CVG", required = true/* , defaultValue = "" */)
	public String getSubServiceTypeDescription() {
		return subServiceTypeDescription;
	}

	public void setSubServiceTypeDescription(String subServiceTypeDescription) {
		this.subServiceTypeDescription = subServiceTypeDescription;
	}

	@JsonProperty("modifiedBy")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SYSTEM", required = true/* , defaultValue = "" */)
	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@JsonProperty("language1Message")
	@io.swagger.v3.oas.annotations.media.Schema(example = "This is from API 5", required = true/* , defaultValue = "" */)
	public String getLanguage1Message() {
		return language1Message;
	}

	public void setLanguage1Message(String language1Message) {
		this.language1Message = language1Message;
	}

	@JsonProperty("language2Message")
	@io.swagger.v3.oas.annotations.media.Schema(example = "This is from API 5", required = true/* , defaultValue = "" */)
	public String getLanguage2Message() {
		return language2Message;
	}

	public void setLanguage2Message(String language2Message) {
		this.language2Message = language2Message;
	}
}

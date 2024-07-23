package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Map;

import com.btsl.common.BaseResponseMultiple;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddServiceKeywordReq {

	@JsonProperty("serviceType")
	private String serviceType;

	@JsonProperty("keyword")
	private String keyword;

	@JsonProperty("messageGatewayType")
	private String messageGatewayType;

	@JsonProperty("receivePort")
	private String receivePort;

	@JsonProperty("name")
	private String name;

	@JsonProperty("status")
	private String status;

	@JsonProperty("menu")
	private String menu;

	@JsonProperty("submenu")
	private String submenu;

	@JsonProperty("allowedVersion")
	private String allowedVersion;

	@JsonProperty("keywordModifyAllow")
	private String keywordModifyAllow;

	@JsonProperty("serviceRequestParameter")
	private String serviceRequestParameter;

	@JsonProperty("gatewayRequestParameter")
	private String gatewayRequestParameter;
	
	@JsonProperty("subKeyWord")
	private String subKeyWord;

	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getMessageGatewayType() {
		return messageGatewayType;
	}

	public void setMessageGatewayType(String messageGatewayType) {
		this.messageGatewayType = messageGatewayType;
	}

	public String getReceivePort() {
		return receivePort;
	}

	public void setReceivePort(String receivePort) {
		this.receivePort = receivePort;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getSubmenu() {
		return submenu;
	}

	public void setSubmenu(String submenu) {
		this.submenu = submenu;
	}

	public String getAllowedVersion() {
		return allowedVersion;
	}

	public void setAllowedVersion(String allowedVersion) {
		this.allowedVersion = allowedVersion;
	}

	public String getKeywordModifyAllow() {
		return keywordModifyAllow;
	}

	public void setKeywordModifyAllow(String keywordModifyAllow) {
		this.keywordModifyAllow = keywordModifyAllow;
	}

	public String getServiceRequestParameter() {
		return serviceRequestParameter;
	}

	public void setServiceRequestParameter(String serviceRequestParameter) {
		this.serviceRequestParameter = serviceRequestParameter;
	}

	public String getGatewayRequestParameter() {
		return gatewayRequestParameter;
	}

	public void setGatewayRequestParameter(String gatewayRequestParameter) {
		this.gatewayRequestParameter = gatewayRequestParameter;
	}

	public String getSubKeyWord() {
		return subKeyWord;
	}

	public void setSubKeyWord(String subKeyWord) {
		this.subKeyWord = subKeyWord;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddServiceKeywordReq").append(", serviceType=").append(serviceType).append(", keyword=")
				.append(keyword).append(", messageGatewayType=").append(messageGatewayType)
				.append(", receivePort=").append(receivePort)
				.append(", name=").append(name)
				.append(", status=").append(status)
				.append(", menu=").append(menu)
				.append(", submenu=").append(submenu)
				.append(", allowedVersion=").append(allowedVersion)
				.append(",keywordModifyAllow=").append(keywordModifyAllow)
				.append(",serviceRequestParameter=").append(serviceRequestParameter)
				.append(",gatewayRequestParameter=").append(gatewayRequestParameter)
				
				.append(", additionalProperties=")
				.append(additionalProperties).append("]");
		return builder.toString();
	}

}

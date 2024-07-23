package com.restapi.o2c.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchFOCFileDownloadRequestVO {
	
	@JsonProperty("domain")
	String domain;
	
	@JsonProperty("category")
	String category;
	
	@JsonProperty("geography")
	String geography;
	
	@JsonProperty("product")
	String product;
	
	@JsonProperty("fileType")
	String fileType;
	
	@JsonProperty("selectedCommissionWallet")
	String selectedCommissionWallet;

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

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	

	@Override
	public String toString() {
		return "BatchFOCUserDownloadListController [domain=" + domain + ", category=" + category + ", geography="
				+ geography + ", product=" + product + "]";
	}

	public String getSelectedCommissionWallet() {
		return selectedCommissionWallet;
	}

	public void setSelectedCommissionWallet(String selectedCommissionWallet) {
		this.selectedCommissionWallet = selectedCommissionWallet;
	}
	
	
	
	

}

package com.restapi.o2c.service;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class O2CBatchTransferRequestVO extends OAuthUser {
  
	@JsonProperty("data")
    private O2CBatchTransferDetails o2CBatchTransferDetails = null;

	@JsonProperty("data")
	public O2CBatchTransferDetails getO2CBatchTransferDetails() {
		return o2CBatchTransferDetails;
	}

	@JsonProperty("data")
	public void setO2CBatchTransferDetails(
			O2CBatchTransferDetails o2cBatchTransferDetails) {
		o2CBatchTransferDetails = o2cBatchTransferDetails;
	}

	@Override
	public String toString() {
		return "O2CBatchTransferRequestVO [o2CBatchTransferDetails="
				+ o2CBatchTransferDetails + "]";
	}
}


	class O2CBatchTransferDetails {
			@JsonProperty("language1")
			private String language1;
			@JsonProperty("language2")
			private String language2;
			@JsonProperty("geographicalDomain")
			private String geographicalDomain;
			@JsonProperty("channelDomain")
			private String channelDomain;
			@JsonProperty("usercategory")
			private String usercategory;
			@JsonProperty("product")
			private String product;
			@JsonProperty("pin")
			private String pin;
			@JsonProperty("batchName")
			private String batchName;
			@io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true, description="Base64 Encoded File as String")
			@JsonProperty("fileAttachment")
			private String fileAttachment;
			
			@io.swagger.v3.oas.annotations.media.Schema(example = "o2cBatchTransfer", required = true, description="File Name")
			@JsonProperty("fileName")
			private String fileName;
			
			@io.swagger.v3.oas.annotations.media.Schema(example = "xls", required = true, description="File Type(csv, xls, xlsx")
			@JsonProperty("fileType")
			private String fileType;
			
			@JsonProperty("language1")
			public String getLanguage1() {
			return language1;
			}
			
			@JsonProperty("language1")
			public void setLanguage1(String language1) {
			this.language1 = language1;
			}
			
			@JsonProperty("language2")
			public String getLanguage2() {
			return language2;
			}
			
			@JsonProperty("language2")
			public void setLanguage2(String language2) {
			this.language2 = language2;
			}
			
			@JsonProperty("geographicalDomain")
			public String getGeographicalDomain() {
			return geographicalDomain;
			}
			
			@JsonProperty("geographicalDomain")
			public void setGeographicalDomain(String geoDomain) {
			this.geographicalDomain = geoDomain;
			}
			
			@JsonProperty("channelDomain")
			public String getChannelDomain() {
			return channelDomain;
			}
			
			@JsonProperty("channelDomain")
			public void setChannelDomain(String channelDomain) {
			this.channelDomain = channelDomain;
			}
			
			@JsonProperty("usercategory")
			public String getUsercategory() {
			return usercategory;
			}
			
			@JsonProperty("usercategory")
			public void setUsercategory(String usercategory) {
			this.usercategory = usercategory;
			}
			
			@JsonProperty("product")
			public String getProduct() {
			return product;
			}
			
			@JsonProperty("product")
			public void setProduct(String product) {
			this.product = product;
			}
			
			@JsonProperty("pin")
			public String getPin() {
			return pin;
			}
			
			@JsonProperty("pin")
			public void setPin(String pin) {
			this.pin = pin;
			}
			
			@JsonProperty("batchName")
			public String getBatchName() {
			return batchName;
			}
			
			@JsonProperty("batchName")
			public void setBatchName(String batchName) {
			this.batchName = batchName;
			}
			
			
			@JsonProperty("fileAttachment")
			public String getFileAttachment() {
				return fileAttachment;
			}

			@JsonProperty("fileAttachment")
			public void setFileAttachment(String fileAttachment) {
				this.fileAttachment = fileAttachment;
			}

			@JsonProperty("fileName")
			public String getFileName() {
			return fileName;
			}
			
			@JsonProperty("fileName")
			public void setFileName(String fileName) {
			this.fileName = fileName;
			}
			
			@JsonProperty("fileType")
			public String getFileType() {
			return fileType;
			}
			
			@JsonProperty("fileType")
			public void setFileType(String fileType) {
			this.fileType = fileType;
			}

			@Override
			public String toString() {
				return "O2CBatchTransferDetails [language1=" + language1
						+ ", language2=" + language2 + ", geoDomain="
						+ geographicalDomain + ", channelDomain=" + channelDomain + ", usercategory="
						+ usercategory + ", product=" + product + ", pin=" + pin
						+ ", batchName=" + batchName + ", fileAttachment=" + fileAttachment
						+ ", fileName=" + fileName + ", fileType=" + fileType
						+ "]";
			}
			
}
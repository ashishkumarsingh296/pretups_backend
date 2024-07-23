
package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "data" })
public class C2CTransfercommDownloadReq {

	@JsonProperty("data")
	private C2CTransfcommDownloadReqData data;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("data")
	public C2CTransfcommDownloadReqData getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(C2CTransfcommDownloadReqData data) {
		this.data = data;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("data", data).append("additionalProperties", additionalProperties)
				.toString();
	}

	public class C2CTransfcommDownloadReqData {

		   @JsonProperty("fromDate")
	        private String fromDate;
	        @JsonProperty("toDate")
	        private String toDate;
	        @JsonProperty("extnwcode")
	        private String extnwcode;
	        @JsonProperty("categoryCode")
	        private String categoryCode;
	        @JsonProperty("domain")
	        private String domain;
	        @JsonProperty("geography")
	        private String geography;
	        @JsonProperty("transferSubType")
	        private String transferSubType;
	        @JsonProperty("transferInout")
	        private String transferInout;
	        @JsonProperty("senderMobileNumber")
	        private String senderMobileNumber;
	        @JsonProperty("receiverMobileNumber")
	        private String receiverMobileNumber;
	        @JsonProperty("includeStaffDetails")
	        private String includeStaffDetails;
	        @JsonProperty("user")
	        private String user;
	        @JsonProperty("transferCategory")
	        private String transferCategory;
	        @JsonProperty("distributionType")
	        private String distributionType;
	        @JsonProperty("transferUserCategory")
	        private String transferUserCategory;
	        @JsonProperty("transferUser")
	        private String transferUser;
	        @JsonProperty("reqTab")
	        private String reqTab;
	        @JsonProperty("fileType")
	        private String fileType;
			@JsonProperty("dispHeaderColumnList")
			private List<DispHeaderColumn> dispHeaderColumnList;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@Override
		public String toString() {
			return "C2CTransfcommDownloadReqData [fromDate=" + fromDate + ", toDate=" + toDate + ", extnwcode=" + extnwcode
					+ "fileType=" + fileType + ", additionalProperties=" + additionalProperties + "]";
		}

		public String getFromDate() {
			return fromDate;
		}

		public void setFromDate(String fromDate) {
			this.fromDate = fromDate;
		}

		public String getToDate() {
			return toDate;
		}

		public void setToDate(String toDate) {
			this.toDate = toDate;
		}

		public String getExtnwcode() {
			return extnwcode;
		}

		public void setExtnwcode(String extnwcode) {
			this.extnwcode = extnwcode;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getFileType() {
			return fileType;
		}

		public void setFileType(String fileType) {
			this.fileType = fileType;
		}

		public List<DispHeaderColumn> getDispHeaderColumnList() {
			return dispHeaderColumnList;
		}

		public void setDispHeaderColumnList(List<DispHeaderColumn> dispHeaderColumnList) {
			this.dispHeaderColumnList = dispHeaderColumnList;
		}

		public Map<String, Object> getAdditionalProperties() {
			return additionalProperties;
		}

		public void setAdditionalProperties(Map<String, Object> additionalProperties) {
			this.additionalProperties = additionalProperties;
		}

		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}



		public String getGeography() {
			return geography;
		}

		public void setGeography(String geography) {
			this.geography = geography;
		}

		public String getTransferSubType() {
			return transferSubType;
		}

		public void setTransferSubType(String transferSubType) {
			this.transferSubType = transferSubType;
		}

		public String getTransferInout() {
			return transferInout;
		}

		public void setTransferInout(String transferInout) {
			this.transferInout = transferInout;
		}

		public String getSenderMobileNumber() {
			return senderMobileNumber;
		}

		public void setSenderMobileNumber(String senderMobileNumber) {
			this.senderMobileNumber = senderMobileNumber;
		}

		public String getReceiverMobileNumber() {
			return receiverMobileNumber;
		}

		public void setReceiverMobileNumber(String receiverMobileNumber) {
			this.receiverMobileNumber = receiverMobileNumber;
		}

		public String getIncludeStaffDetails() {
			return includeStaffDetails;
		}

		public void setIncludeStaffDetails(String includeStaffDetails) {
			this.includeStaffDetails = includeStaffDetails;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		
		public String getTransferUser() {
			return transferUser;
		}

		public void setTransferUser(String transferUser) {
			this.transferUser = transferUser;
		}

		public String getReqTab() {
			return reqTab;
		}

		public String getTransferCategory() {
			return transferCategory;
		}

		public void setTransferCategory(String transferCategory) {
			this.transferCategory = transferCategory;
		}

		public String getDistributionType() {
			return distributionType;
		}

		public void setDistributionType(String distributionType) {
			this.distributionType = distributionType;
		}

		public void setReqTab(String reqTab) {
			this.reqTab = reqTab;
		}

		public String getTransferUserCategory() {
			return transferUserCategory;
		}

		public void setTransferUserCategory(String transferUserCategory) {
			this.transferUserCategory = transferUserCategory;
		}

	

	}

}

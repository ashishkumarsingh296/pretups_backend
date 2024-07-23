
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
public class C2STransfercommDownloadReq {

	@JsonProperty("data")
	private C2sTransfcommDownloadReqData data;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("data")
	public C2sTransfcommDownloadReqData getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(C2sTransfcommDownloadReqData data) {
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

	public class C2sTransfcommDownloadReqData {

		@JsonProperty("reqTab")
		@io.swagger.v3.oas.annotations.media.Schema(example = "C2C_MOBILENUMB_TAB_REQ/C2C_ADVANCED_TAB_REQ", required = true/* , defaultValue = "" */)
		private String reqTab;
		@JsonProperty("reportDate")
		@io.swagger.v3.oas.annotations.media.Schema(example = "29/06/21", required = true/* , defaultValue = "" */)
		private String reportDate;
		@JsonProperty("allowedTimeFrom")
		@io.swagger.v3.oas.annotations.media.Schema(example = "10:20", required = true/* , defaultValue = "" */)
		private String allowedTimeFrom;
		@JsonProperty("allowedTimeTo")
		@io.swagger.v3.oas.annotations.media.Schema(example = "23:59", required = true/* , defaultValue = "" */)
		private String allowedTimeTo;
		@JsonProperty("extnwcode")
		@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
		private String networkCode;
		@JsonProperty("categoryCode")
		@io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */)
		private String categoryCode;
		@JsonProperty("domain")
		@io.swagger.v3.oas.annotations.media.Schema(example = "DIST", required = true/* , defaultValue = "" */)
		private String domain;
		@JsonProperty("service")
		@io.swagger.v3.oas.annotations.media.Schema(example = "RC/ALL", required = true/* , defaultValue = "" */)
		private String service;
		@JsonProperty("transStatus")
		@io.swagger.v3.oas.annotations.media.Schema(example = "200/ALL", required = true/* , defaultValue = "" */)
		private String transStatus;
		@JsonProperty("fileType")
		@io.swagger.v3.oas.annotations.media.Schema(example = "CSV/XLSX/PDF", required = true/* , defaultValue = "" */)
		private String fileType;
		@JsonProperty("geography")
		@io.swagger.v3.oas.annotations.media.Schema(example = "HARYANA/ALL", required = true/* , defaultValue = "" */)
		private String geography;
		@JsonProperty("userType")
		@io.swagger.v3.oas.annotations.media.Schema(example = "STAFF/CHANNEL", required = true/* , defaultValue = "" */)
		private String userType;
		@JsonProperty("mobileNumber")
		@io.swagger.v3.oas.annotations.media.Schema(example = "84938948984", required = true/* , defaultValue = "" */)
		private String mobileNumber;
		@JsonProperty("channelUser")
		@io.swagger.v3.oas.annotations.media.Schema(example = "Nuer878947832488", required = true/* , defaultValue = "" */)
		private String channelUser;
		@JsonProperty("staffOption")
		@io.swagger.v3.oas.annotations.media.Schema(example = "OPTION_LOGIN_ID/OPTION_MSISDN", required = true/* , defaultValue = "" */)
		private String staffOption;
		@JsonProperty("loginIDOrMSISDN")
		@io.swagger.v3.oas.annotations.media.Schema(example = "rary_dist/738363773622", required = true/* , defaultValue = "" */)
		private String loginIDOrMSISDN;

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

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}

		public String getTransStatus() {
			return transStatus;
		}

		public void setTransStatus(String transStatus) {
			this.transStatus = transStatus;
		}

		public String getGeography() {
			return geography;
		}

		public void setGeography(String geography) {
			this.geography = geography;
		}

		public String getReqTab() {
			return reqTab;
		}

		public void setReqTab(String reqTab) {
			this.reqTab = reqTab;
		}

		public String getReportDate() {
			return reportDate;
		}

		public void setReportDate(String reportDate) {
			this.reportDate = reportDate;
		}

		public String getAllowedTimeFrom() {
			return allowedTimeFrom;
		}

		public void setAllowedTimeFrom(String allowedTimeFrom) {
			this.allowedTimeFrom = allowedTimeFrom;
		}

		public String getAllowedTimeTo() {
			return allowedTimeTo;
		}

		public void setAllowedTimeTo(String allowedTimeTo) {
			this.allowedTimeTo = allowedTimeTo;
		}

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}

		public String getMobileNumber() {
			return mobileNumber;
		}

		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		public String getChannelUser() {
			return channelUser;
		}

		public void setChannelUser(String channelUser) {
			this.channelUser = channelUser;
		}

		public String getStaffOption() {
			return staffOption;
		}

		public void setStaffOption(String staffOption) {
			this.staffOption = staffOption;
		}

		public String getLoginIDOrMSISDN() {
			return loginIDOrMSISDN;
		}

		public void setLoginIDOrMSISDN(String loginIDOrMSISDN) {
			this.loginIDOrMSISDN = loginIDOrMSISDN;
		}

		public String getNetworkCode() {
			return networkCode;
		}

		public void setNetworkCode(String networkCode) {
			this.networkCode = networkCode;
		}

		@JsonProperty("dispHeaderColumnList")
		private List<DispHeaderColumn> dispHeaderColumnList;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@Override
		public String toString() {
			return "PassbookDownloadReqData [reportDate=" + reportDate + ", allowedTimeFrom=" + allowedTimeFrom
					+ ", allowedTimeTo=" + allowedTimeTo + "fileType=" + fileType + ", additionalProperties="
					+ additionalProperties + "]";
		}

	}

}

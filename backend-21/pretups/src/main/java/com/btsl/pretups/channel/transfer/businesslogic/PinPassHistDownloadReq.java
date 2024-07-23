
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
public class PinPassHistDownloadReq {

	@JsonProperty("data")
	private PinPassHistDownloadReqData data;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("data")
	public PinPassHistDownloadReqData getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(PinPassHistDownloadReqData data) {
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

	public class PinPassHistDownloadReqData {

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
		@JsonProperty("userType")
		private String userType;
		@JsonProperty("reqType")
		private String reqType;
		@JsonProperty("fileType")
		private String fileType;
		@JsonProperty("dispHeaderColumnList")
		private List<DispHeaderColumn> dispHeaderColumnList;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		@Override
		public String toString() {
			return "PassbookDownloadReqData [fromDate=" + fromDate + ", toDate=" + toDate + ", extnwcode=" + extnwcode
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

		public String getUserType() {
			return userType;
		}

		public void setUserType(String userType) {
			this.userType = userType;
		}

		public String getReqType() {
			return reqType;
		}

		public void setReqType(String reqType) {
			this.reqType = reqType;
		}

	}

}

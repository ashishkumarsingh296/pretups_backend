package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
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

public class AdditionalCommSummryC2SReqVO {

	@JsonProperty("data")
	private AdditionalCommSummryC2SReqData data;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("data")
	public AdditionalCommSummryC2SReqData getData() {
		return data;
	}

	@JsonProperty("data")
	public void setData(AdditionalCommSummryC2SReqData data) {
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

	public class AdditionalCommSummryC2SReqData {

		@JsonProperty("domain")
		private String domain;
		@JsonProperty("categoryCode")
		private String categoryCode;
		@JsonProperty("geography")
		private String geography;
		@JsonProperty("dailyOrmonthlyOption")
		private String dailyOrmonthlyOption;
		@JsonProperty("fromDate")
		private String fromDate;
		@JsonProperty("toDate")
		private String toDate;
		@JsonProperty("fromMonthYear")
		private String fromMonthYear;
		@JsonProperty("toMonthYear")
		private String toMonthYear;
		@JsonProperty("service")
		private String service;

		@JsonIgnore
		private Map<String, Object> additionalProperties = new HashMap<String, Object>();

		public Map<String, Object> getAdditionalProperties() {
			return additionalProperties;
		}

		public void setAdditionalProperties(Map<String, Object> additionalProperties) {
			this.additionalProperties = additionalProperties;
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

		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public String getGeography() {
			return geography;
		}

		public void setGeography(String geography) {
			this.geography = geography;
		}

		public String getDailyOrmonthlyOption() {
			return dailyOrmonthlyOption;
		}

		public void setDailyOrmonthlyOption(String dailyOrmonthlyOption) {
			this.dailyOrmonthlyOption = dailyOrmonthlyOption;
		}

		public String getFromMonthYear() {
			return fromMonthYear;
		}

		public void setFromMonthYear(String fromMonthYear) {
			this.fromMonthYear = fromMonthYear;
		}

		public String getToMonthYear() {
			return toMonthYear;
		}

		public void setToMonthYear(String toMonthYear) {
			this.toMonthYear = toMonthYear;
		}

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}
		

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("O2CTransferdetSearchReqData[ fromDate=").append(fromDate);
			sb.append(",toDate=").append(toDate);
			sb.append(",categoryCode=").append(categoryCode);
			sb.append(",domain=").append(domain);
			sb.append(",geography=").append(geography);
			sb.append(",dailyOrmonthlyOption=").append(dailyOrmonthlyOption);
			sb.append(",service=").append(service);
			sb.append(",fromMonthYear=").append(fromMonthYear);
			sb.append(",toMonthYear=").append(toMonthYear);
			sb.append("]");
			return sb.toString();
		}


	}

}

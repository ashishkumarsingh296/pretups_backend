package com.restapi.reportsDownload;

import com.fasterxml.jackson.annotation.JsonProperty;


import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.restapi.user.service.HeaderColumn;

import java.util.List;
import java.util.Optional;

public class BarredUserListRequestVO {
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("fromDate")
	private String fromDate;
	@JsonProperty("todate")
	private String todate;
	@JsonProperty("domain")
	private String domain;
	@JsonProperty("category")
	private String category;
	@JsonProperty("geography")
	private String geography;
	@JsonProperty("userType")
	private String userType;
	@JsonProperty("module")
	private String module;
	@JsonProperty("barredAs")
	private String barredAs;
	@JsonProperty("barredtype")
	private String barredtype;

	private List<HeaderColumn> headerColumns;

	public List<HeaderColumn> getHeaderColumns() {
		return headerColumns;
	}

	public void setHeaderColumns(List<HeaderColumn> headerColumns) {
		this.headerColumns = headerColumns;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getTodate() {
		return todate;
	}

	public void setTodate(String todate) {
		this.todate = todate;
	}

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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getBarredAs() {
		return barredAs;
	}

	public void setBarredAs(String barredAs) {
		this.barredAs = barredAs;
	}

	public String getBarredtype() {
		return barredtype;
	}

	public void setBarredtype(String barredtype) {
		this.barredtype = barredtype;
	}

	@Override
	public String toString() {
		return "BarredUserListRequestVO [msisdn=" + msisdn + ", userName=" + userName + ", fromDate=" + fromDate
				+ ", todate=" + todate + ", domain=" + domain + ", category=" + category + ", geography=" + geography
				+ ", userType=" + userType + ", module=" + module + ", barredAs=" + barredAs + ", barredtype="
				+ barredtype + ", headerColumns=" + headerColumns + "]";
	}

}

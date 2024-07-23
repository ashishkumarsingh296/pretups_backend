package restassuredapi.pojo.fetchbarreduserlistrequestpojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"barredAs",
"barredtype",
"category",
"domain",
"fromDate",
"geography",
"module",
"msisdn",
"todate",
"userName",
"userType"
})
@Generated("jsonschema2pojo")
	public class FetchBarredListRequestPojo {
	
	@JsonProperty("barredAs")
	private String barredAs;
	@JsonProperty("barredtype")
	private String barredtype;
	@JsonProperty("category")
	private String category;
	@JsonProperty("domain")
	private String domain;
	@JsonProperty("fromDate")
	private String fromDate;
	@JsonProperty("geography")
	private String geography;
	@JsonProperty("module")
	private String module;
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("todate")
	private String todate;
	@JsonProperty("userName")
	private String userName;
	@JsonProperty("userType")
	private String userType;
	
	@JsonProperty("barredAs")
	public String getBarredAs() {
	return barredAs;
	}
	
	@JsonProperty("barredAs")
	public void setBarredAs(String barredAs) {
	this.barredAs = barredAs;
	}
	
	@JsonProperty("barredtype")
	public String getBarredtype() {
	return barredtype;
	}
	
	@JsonProperty("barredtype")
	public void setBarredtype(String barredtype) {
	this.barredtype = barredtype;
	}
	
	@JsonProperty("category")
	public String getCategory() {
	return category;
	}
	
	@JsonProperty("category")
	public void setCategory(String category) {
	this.category = category;
	}
	
	@JsonProperty("domain")
	public String getDomain() {
	return domain;
	}
	
	@JsonProperty("domain")
	public void setDomain(String domain) {
	this.domain = domain;
	}
	
	@JsonProperty("fromDate")
	public String getFromDate() {
	return fromDate;
	}
	
	@JsonProperty("fromDate")
	public void setFromDate(String fromDate) {
	this.fromDate = fromDate;
	}
	
	@JsonProperty("geography")
	public String getGeography() {
	return geography;
	}
	
	@JsonProperty("geography")
	public void setGeography(String geography) {
	this.geography = geography;
	}
	
	@JsonProperty("module")
	public String getModule() {
	return module;
	}
	
	@JsonProperty("module")
	public void setModule(String module) {
	this.module = module;
	}
	
	@JsonProperty("msisdn")
	public String getMsisdn() {
	return msisdn;
	}
	
	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
	}
	
	@JsonProperty("todate")
	public String getTodate() {
	return todate;
	}
	
	@JsonProperty("todate")
	public void setTodate(String todate) {
	this.todate = todate;
	}
	
	@JsonProperty("userName")
	public String getUserName() {
	return userName;
	}
	
	@JsonProperty("userName")
	public void setUserName(String userName) {
	this.userName = userName;
	}
	
	@JsonProperty("userType")
	public String getUserType() {
	return userType;
	}
	
	@JsonProperty("userType")
	public void setUserType(String userType) {
	this.userType = userType;
	}
	
}
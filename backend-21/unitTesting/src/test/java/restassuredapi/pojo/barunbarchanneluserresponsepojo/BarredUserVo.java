package restassuredapi.pojo.barunbarchanneluserresponsepojo;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"userTypeName",
"barredTypeList",
"serviceType",
"forMsisdn",
"name",
"module",
"moduleName",
"barredType",
"barredReason",
"multiBox",
"modifiedBy",
"networkCode",
"fromDate",
"toDate",
"networkName",
"bar",
"userTypeDesc",
"barredDate",
"userType",
"msisdn",
"createdOn",
"modifiedOn",
"createdBy",
"barredTypeName",
"domainCode",
"domainName",
"categoryCode",
"barredAs",
"categoryName",
"loginId"
})
@Generated("jsonschema2pojo")
	public class BarredUserVo {
	
	@JsonProperty("userTypeName")
	private Object userTypeName;
	@JsonProperty("barredTypeList")
	private Object barredTypeList;
	@JsonProperty("serviceType")
	private Object serviceType;
	@JsonProperty("forMsisdn")
	private String forMsisdn;
	@JsonProperty("name")
	private String name;
	@JsonProperty("module")
	private String module;
	@JsonProperty("moduleName")
	private String moduleName;
	@JsonProperty("barredType")
	private String barredType;
	@JsonProperty("barredReason")
	private String barredReason;
	@JsonProperty("multiBox")
	private Object multiBox;
	@JsonProperty("modifiedBy")
	private Object modifiedBy;
	@JsonProperty("networkCode")
	private Object networkCode;
	@JsonProperty("fromDate")
	private Object fromDate;
	@JsonProperty("toDate")
	private Object toDate;
	@JsonProperty("networkName")
	private String networkName;
	@JsonProperty("bar")
	private Boolean bar;
	@JsonProperty("userTypeDesc")
	private Object userTypeDesc;
	@JsonProperty("barredDate")
	private String barredDate;
	@JsonProperty("userType")
	private String userType;
	@JsonProperty("msisdn")
	private String msisdn;
	@JsonProperty("createdOn")
	private Long createdOn;
	@JsonProperty("modifiedOn")
	private Object modifiedOn;
	@JsonProperty("createdBy")
	private String createdBy;
	@JsonProperty("barredTypeName")
	private String barredTypeName;
	@JsonProperty("domainCode")
	private String domainCode;
	@JsonProperty("domainName")
	private String domainName;
	@JsonProperty("categoryCode")
	private String categoryCode;
	@JsonProperty("barredAs")
	private String barredAs;
	@JsonProperty("categoryName")
	private String categoryName;
	@JsonProperty("loginId")
	private String loginId;
	
	@JsonProperty("userTypeName")
	public Object getUserTypeName() {
	return userTypeName;
	}
	
	@JsonProperty("userTypeName")
	public void setUserTypeName(Object userTypeName) {
	this.userTypeName = userTypeName;
	}
	
	
	@JsonProperty("barredTypeList")
	public Object getBarredTypeList() {
	return barredTypeList;
	}
	
	@JsonProperty("barredTypeList")
	public void setBarredTypeList(Object barredTypeList) {
	this.barredTypeList = barredTypeList;
	}
	
	@JsonProperty("serviceType")
	public Object getServiceType() {
	return serviceType;
	}
	
	@JsonProperty("serviceType")
	public void setServiceType(Object serviceType) {
	this.serviceType = serviceType;
	}
	
	
	@JsonProperty("forMsisdn")
	public String getForMsisdn() {
	return forMsisdn;
	}
	
	@JsonProperty("forMsisdn")
	public void setForMsisdn(String forMsisdn) {
	this.forMsisdn = forMsisdn;
	}
	
	@JsonProperty("name")
	public String getName() {
	return name;
	}
	
	@JsonProperty("name")
	public void setName(String name) {
	this.name = name;
	}
	
	@JsonProperty("module")
	public String getModule() {
	return module;
	}
	
	@JsonProperty("module")
	public void setModule(String module) {
	this.module = module;
	}
	
	@JsonProperty("moduleName")
	public String getModuleName() {
	return moduleName;
	}
	
	@JsonProperty("moduleName")
	public void setModuleName(String moduleName) {
	this.moduleName = moduleName;
	}
	
	@JsonProperty("barredType")
	public String getBarredType() {
	return barredType;
	}
	
	@JsonProperty("barredType")
	public void setBarredType(String barredType) {
	this.barredType = barredType;
	}
	
	@JsonProperty("barredReason")
	public String getBarredReason() {
	return barredReason;
	}
	
	@JsonProperty("barredReason")
	public void setBarredReason(String barredReason) {
	this.barredReason = barredReason;
	}
	
	
	@JsonProperty("multiBox")
	public Object getMultiBox() {
	return multiBox;
	}
	
	@JsonProperty("multiBox")
	public void setMultiBox(Object multiBox) {
	this.multiBox = multiBox;
	}
	
	
	@JsonProperty("modifiedBy")
	public Object getModifiedBy() {
	return modifiedBy;
	}
	
	@JsonProperty("modifiedBy")
	public void setModifiedBy(Object modifiedBy) {
	this.modifiedBy = modifiedBy;
	}
	
	@JsonProperty("networkCode")
	public Object getNetworkCode() {
	return networkCode;
	}
	
	@JsonProperty("networkCode")
	public void setNetworkCode(Object networkCode) {
	this.networkCode = networkCode;
	}
	
	
	@JsonProperty("fromDate")
	public Object getFromDate() {
	return fromDate;
	}
	
	@JsonProperty("fromDate")
	public void setFromDate(Object fromDate) {
	this.fromDate = fromDate;
	}
	
	
	@JsonProperty("toDate")
	public Object getToDate() {
	return toDate;
	}
	
	@JsonProperty("toDate")
	public void setToDate(Object toDate) {
	this.toDate = toDate;
	}
	
	
	@JsonProperty("networkName")
	public String getNetworkName() {
	return networkName;
	}
	
	@JsonProperty("networkName")
	public void setNetworkName(String networkName) {
	this.networkName = networkName;
	}
	
	@JsonProperty("bar")
	public Boolean getBar() {
	return bar;
	}
	
	@JsonProperty("bar")
	public void setBar(Boolean bar) {
	this.bar = bar;
	}
	
	
	@JsonProperty("userTypeDesc")
	public Object getUserTypeDesc() {
	return userTypeDesc;
	}
	
	@JsonProperty("userTypeDesc")
	public void setUserTypeDesc(Object userTypeDesc) {
	this.userTypeDesc = userTypeDesc;
	}
	
	
	@JsonProperty("barredDate")
	public String getBarredDate() {
	return barredDate;
	}
	
	@JsonProperty("barredDate")
	public void setBarredDate(String barredDate) {
	this.barredDate = barredDate;
	}
	
	
	@JsonProperty("userType")
	public String getUserType() {
	return userType;
	}
	
	@JsonProperty("userType")
	public void setUserType(String userType) {
	this.userType = userType;
	}
	
	
	@JsonProperty("msisdn")
	public String getMsisdn() {
	return msisdn;
	}
	
	@JsonProperty("msisdn")
	public void setMsisdn(String msisdn) {
	this.msisdn = msisdn;
	}
	
	@JsonProperty("createdOn")
	public Long getCreatedOn() {
	return createdOn;
	}
	
	@JsonProperty("createdOn")
	public void setCreatedOn(Long createdOn) {
	this.createdOn = createdOn;
	}
	
	@JsonProperty("modifiedOn")
	public Object getModifiedOn() {
	return modifiedOn;
	}
	
	@JsonProperty("modifiedOn")
	public void setModifiedOn(Object modifiedOn) {
	this.modifiedOn = modifiedOn;
	}
	
	@JsonProperty("createdBy")
	public String getCreatedBy() {
	return createdBy;
	}
	
	@JsonProperty("createdBy")
	public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
	}
	
	@JsonProperty("barredTypeName")
	public String getBarredTypeName() {
	return barredTypeName;
	}
	
	@JsonProperty("barredTypeName")
	public void setBarredTypeName(String barredTypeName) {
	this.barredTypeName = barredTypeName;
	}
	
	@JsonProperty("domainCode")
	public String getDomainCode() {
	return domainCode;
	}
	
	@JsonProperty("domainCode")
	public void setDomainCode(String domainCode) {
	this.domainCode = domainCode;
	}
	
	@JsonProperty("domainName")
	public String getDomainName() {
	return domainName;
	}
	
	@JsonProperty("domainName")
	public void setDomainName(String domainName) {
	this.domainName = domainName;
	}
	
	
	@JsonProperty("categoryCode")
	public String getCategoryCode() {
	return categoryCode;
	}
	
	@JsonProperty("categoryCode")
	public void setCategoryCode(String categoryCode) {
	this.categoryCode = categoryCode;
	}
	
	
	@JsonProperty("barredAs")
	public String getBarredAs() {
	return barredAs;
	}
	
	@JsonProperty("barredAs")
	public void setBarredAs(String barredAs) {
	this.barredAs = barredAs;
	}
	
	
	@JsonProperty("categoryName")
	public String getCategoryName() {
	return categoryName;
	}
	
	@JsonProperty("categoryName")
	public void setCategoryName(String categoryName) {
	this.categoryName = categoryName;
	}
	
	
	@JsonProperty("loginId")
	public String getLoginId() {
	return loginId;
	}
	
	@JsonProperty("loginId")
	public void setLoginId(String loginId) {
	this.loginId = loginId;
	}
	
}

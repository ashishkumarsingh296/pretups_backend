package restassuredapi.pojo.c2ctransferapprovallistrequestpojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"approvalLevel",
"category",
"domain",
"entriesPerPage",
"extnwcode",
"extrefnum",
"geographicalDomain",
"msisdn2",
"pageNumber",
"requestType",
"transactionId",
"transferType",
"usernameToSearch"
})
public class Data {

@JsonProperty("approvalLevel")
private String approvalLevel;
@JsonProperty("category")
private String category;
@JsonProperty("domain")
private String domain;
@JsonProperty("entriesPerPage")
private String entriesPerPage;
@JsonProperty("extnwcode")
private String extnwcode;
@JsonProperty("extrefnum")
private String extrefnum;
@JsonProperty("geographicalDomain")
private String geographicalDomain;
@JsonProperty("msisdn2")
private String msisdn2;
@JsonProperty("pageNumber")
private String pageNumber;
@JsonProperty("requestType")
private String requestType;
@JsonProperty("transactionId")
private String transactionId;
@JsonProperty("transferType")
private String transferType;
@JsonProperty("usernameToSearch")
private String usernameToSearch;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("approvalLevel")
public String getApprovalLevel() {
return approvalLevel;
}

@JsonProperty("approvalLevel")
public void setApprovalLevel(String approvalLevel) {
this.approvalLevel = approvalLevel;
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

@JsonProperty("entriesPerPage")
public String getEntriesPerPage() {
return entriesPerPage;
}

@JsonProperty("entriesPerPage")
public void setEntriesPerPage(String entriesPerPage) {
this.entriesPerPage = entriesPerPage;
}

@JsonProperty("extnwcode")
public String getExtnwcode() {
return extnwcode;
}

@JsonProperty("extnwcode")
public void setExtnwcode(String extnwcode) {
this.extnwcode = extnwcode;
}

@JsonProperty("extrefnum")
public String getExtrefnum() {
return extrefnum;
}

@JsonProperty("extrefnum")
public void setExtrefnum(String extrefnum) {
this.extrefnum = extrefnum;
}

@JsonProperty("geographicalDomain")
public String getGeographicalDomain() {
return geographicalDomain;
}

@JsonProperty("geographicalDomain")
public void setGeographicalDomain(String geographicalDomain) {
this.geographicalDomain = geographicalDomain;
}

@JsonProperty("msisdn2")
public String getMsisdn2() {
return msisdn2;
}

@JsonProperty("msisdn2")
public void setMsisdn2(String msisdn2) {
this.msisdn2 = msisdn2;
}

@JsonProperty("pageNumber")
public String getPageNumber() {
return pageNumber;
}

@JsonProperty("pageNumber")
public void setPageNumber(String pageNumber) {
this.pageNumber = pageNumber;
}

@JsonProperty("requestType")
public String getRequestType() {
return requestType;
}

@JsonProperty("requestType")
public void setRequestType(String requestType) {
this.requestType = requestType;
}

@JsonProperty("transactionId")
public String getTransactionId() {
return transactionId;
}

@JsonProperty("transactionId")
public void setTransactionId(String transactionId) {
this.transactionId = transactionId;
}

@JsonProperty("transferType")
public String getTransferType() {
return transferType;
}

@JsonProperty("transferType")
public void setTransferType(String transferType) {
this.transferType = transferType;
}

@JsonProperty("usernameToSearch")
public String getUsernameToSearch() {
return usernameToSearch;
}

@JsonProperty("usernameToSearch")
public void setUsernameToSearch(String usernameToSearch) {
this.usernameToSearch = usernameToSearch;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}
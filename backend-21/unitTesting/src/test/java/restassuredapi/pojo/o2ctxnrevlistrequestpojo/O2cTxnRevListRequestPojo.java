package restassuredapi.pojo.o2ctxnrevlistrequestpojo;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"category",
"domain",
"fromDate",
"geography",
"msisdn",
"ownerUserId",
"ownerUsername",
"toDate",
"transactionID",
"transferCategory",
"userId",
"userName"
})
@Generated("jsonschema2pojo")
public class O2cTxnRevListRequestPojo {

@JsonProperty("category")
private String category;
@JsonProperty("domain")
private String domain;
@JsonProperty("fromDate")
private String fromDate;
@JsonProperty("geography")
private String geography;
@JsonProperty("msisdn")
private String msisdn;
@JsonProperty("ownerUserId")
private String ownerUserId;
@JsonProperty("ownerUsername")
private String ownerUsername;
@JsonProperty("toDate")
private String toDate;
@JsonProperty("transactionID")
private String transactionID;
@JsonProperty("transferCategory")
private String transferCategory;
@JsonProperty("userId")
private String userId;
@JsonProperty("userName")
private String userName;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

@JsonProperty("msisdn")
public String getMsisdn() {
return msisdn;
}

@JsonProperty("msisdn")
public void setMsisdn(String msisdn) {
this.msisdn = msisdn;
}

@JsonProperty("ownerUserId")
public String getOwnerUserId() {
return ownerUserId;
}

@JsonProperty("ownerUserId")
public void setOwnerUserId(String ownerUserId) {
this.ownerUserId = ownerUserId;
}

@JsonProperty("ownerUsername")
public String getOwnerUsername() {
return ownerUsername;
}

@JsonProperty("ownerUsername")
public void setOwnerUsername(String ownerUsername) {
this.ownerUsername = ownerUsername;
}

@JsonProperty("toDate")
public String getToDate() {
return toDate;
}

@JsonProperty("toDate")
public void setToDate(String toDate) {
this.toDate = toDate;
}

@JsonProperty("transactionID")
public String getTransactionID() {
return transactionID;
}

@JsonProperty("transactionID")
public void setTransactionID(String transactionID) {
this.transactionID = transactionID;
}

@JsonProperty("transferCategory")
public String getTransferCategory() {
return transferCategory;
}

@JsonProperty("transferCategory")
public void setTransferCategory(String transferCategory) {
this.transferCategory = transferCategory;
}

@JsonProperty("userId")
public String getUserId() {
return userId;
}

@JsonProperty("userId")
public void setUserId(String userId) {
this.userId = userId;
}

@JsonProperty("userName")
public String getUserName() {
return userName;
}

@JsonProperty("userName")
public void setUserName(String userName) {
this.userName = userName;
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
package restassuredapi.pojo.c2cbuyvouchercountinforesponsepojo;

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
"category",
"domain",
"eTopUpBalance",
"msisdn",
"ownerName",
"parentName",
"status",
"userName"
})
public class C2CBuyVoucherCountInfoResponse {

@JsonProperty("category")
private String category;
@JsonProperty("domain")
private String domain;
@JsonProperty("eTopUpBalance")
private String eTopUpBalance;
@JsonProperty("msisdn")
private String msisdn;
@JsonProperty("ownerName")
private String ownerName;
@JsonProperty("parentName")
private String parentName;
@JsonProperty("status")
private String status;
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

@JsonProperty("eTopUpBalance")
public String getETopUpBalance() {
return eTopUpBalance;
}

@JsonProperty("eTopUpBalance")
public void setETopUpBalance(String eTopUpBalance) {
this.eTopUpBalance = eTopUpBalance;
}

@JsonProperty("msisdn")
public String getMsisdn() {
return msisdn;
}

@JsonProperty("msisdn")
public void setMsisdn(String msisdn) {
this.msisdn = msisdn;
}

@JsonProperty("ownerName")
public String getOwnerName() {
return ownerName;
}

@JsonProperty("ownerName")
public void setOwnerName(String ownerName) {
this.ownerName = ownerName;
}

@JsonProperty("parentName")
public String getParentName() {
return parentName;
}

@JsonProperty("parentName")
public void setParentName(String parentName) {
this.parentName = parentName;
}

@JsonProperty("status")
public String getStatus() {
return status;
}

@JsonProperty("status")
public void setStatus(String status) {
this.status = status;
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
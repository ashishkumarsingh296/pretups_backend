package restassuredapi.pojo.o2cwithdrawalrequestpojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"fromUserId",
"language",
"pin",
"products",
"remarks",
"walletType"
})
public class O2CWithdrawalRequestData {

@JsonProperty("fromUserId")
private String fromUserId;
@JsonProperty("language")
private String language;
@JsonProperty("pin")
private String pin;
@JsonProperty("products")
private List<ProductW> products = null;
@JsonProperty("remarks")
private String remarks;
@JsonProperty("walletType")
private String walletType;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("fromUserId")
public String getFromUserId() {
return fromUserId;
}

@JsonProperty("fromUserId")
public void setFromUserId(String fromUserId) {
this.fromUserId = fromUserId;
}

@JsonProperty("language")
public String getLanguage() {
return language;
}

@JsonProperty("language")
public void setLanguage(String language) {
this.language = language;
}

@JsonProperty("pin")
public String getPin() {
return pin;
}

@JsonProperty("pin")
public void setPin(String pin) {
this.pin = pin;
}

@JsonProperty("products")
public List<ProductW> getProducts() {
return products;
}

@JsonProperty("products")
public void setProducts(List<ProductW> products) {
this.products = products;
}

@JsonProperty("remarks")
public String getRemarks() {
return remarks;
}

@JsonProperty("remarks")
public void setRemarks(String remarks) {
this.remarks = remarks;
}

@JsonProperty("walletType")
public String getWalletType() {
return walletType;
}

@JsonProperty("walletType")
public void setWalletType(String walletType) {
this.walletType = walletType;
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


package restassuredapi.pojo.c2cstockreturnrequestpojo;

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
"date",
"extcode",
"extcode2",
"extnwcode",
"extrefnum",
"language1",
"loginid",
"loginid2",
"msisdn",
"msisdn2",
"password",
"pin",
"products"
})
public class DataR {

@JsonProperty("date")
private String date;
@JsonProperty("extcode")
private String extcode;
@JsonProperty("extcode2")
private String extcode2;
@JsonProperty("extnwcode")
private String extnwcode;
@JsonProperty("extrefnum")
private String extrefnum;
@JsonProperty("language1")
private String language1;
@JsonProperty("loginid")
private String loginid;
@JsonProperty("loginid2")
private String loginid2;
@JsonProperty("msisdn")
private String msisdn;
@JsonProperty("msisdn2")
private String msisdn2;
@JsonProperty("password")
private String password;
@JsonProperty("pin")
private String pin;
@JsonProperty("products")
private List<ProductR> products = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("date")
public String getDate() {
return date;
}

@JsonProperty("date")
public void setDate(String date) {
this.date = date;
}

@JsonProperty("extcode")
public String getExtcode() {
return extcode;
}

@JsonProperty("extcode")
public void setExtcode(String extcode) {
this.extcode = extcode;
}

@JsonProperty("extcode2")
public String getExtcode2() {
return extcode2;
}

@JsonProperty("extcode2")
public void setExtcode2(String extcode2) {
this.extcode2 = extcode2;
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

@JsonProperty("language1")
public String getLanguage1() {
return language1;
}

@JsonProperty("language1")
public void setLanguage1(String language1) {
this.language1 = language1;
}

@JsonProperty("loginid")
public String getLoginid() {
return loginid;
}

@JsonProperty("loginid")
public void setLoginid(String loginid) {
this.loginid = loginid;
}

@JsonProperty("loginid2")
public String getLoginid2() {
return loginid2;
}

@JsonProperty("loginid2")
public void setLoginid2(String loginid2) {
this.loginid2 = loginid2;
}

@JsonProperty("msisdn")
public String getMsisdn() {
return msisdn;
}

@JsonProperty("msisdn")
public void setMsisdn(String msisdn) {
this.msisdn = msisdn;
}

@JsonProperty("msisdn2")
public String getMsisdn2() {
return msisdn2;
}

@JsonProperty("msisdn2")
public void setMsisdn2(String msisdn2) {
this.msisdn2 = msisdn2;
}

@JsonProperty("password")
public String getPassword() {
return password;
}

@JsonProperty("password")
public void setPassword(String password) {
this.password = password;
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
public List<ProductR> getProducts() {
return products;
}

@JsonProperty("products")
public void setProducts(List<ProductR> products) {
this.products = products;
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


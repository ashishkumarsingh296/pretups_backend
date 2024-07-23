
package com.restapi.user.service;

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
    "extnwcode",
    "msisdn",
    "pin",
    "loginid",
    "password",
    "extcode",
    "extrefnum",
    "msisdn2",
    "loginid2",
    "extcode2",
    "products",
    "language1"
})
public class C2CReturnWithdrawData {

    @JsonProperty("date")
    @io.swagger.v3.oas.annotations.media.Schema(example = "22/06/20", required= true, description = "Source Type")
    private String date;
    @JsonProperty("extnwcode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required= true, description = "Source Type")
    private String extnwcode;
    @JsonProperty("msisdn")
    @io.swagger.v3.oas.annotations.media.Schema(example = "7243535653", required= true, description = "Source Type")
    private String msisdn;
    @JsonProperty("pin")
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required= true, description = "Source Type")
    private String pin;
    @JsonProperty("loginid")
    @io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required= true, description = "Source Type")
    private String loginid;
    @JsonProperty("password")
    @io.swagger.v3.oas.annotations.media.Schema(example = "2468", required= true, description = "Source Type")
    private String password;
    @JsonProperty("extcode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "5226532", required= true, description = "Source Type")
    private String extcode;
    @JsonProperty("extrefnum")
    @io.swagger.v3.oas.annotations.media.Schema(example = "52425625", required= true, description = "Source Type")
    private String extrefnum;
    @JsonProperty("msisdn2")
    @io.swagger.v3.oas.annotations.media.Schema(example = "725465456", required= true, description = "Source Type")
    private String msisdn2;
    @JsonProperty("loginid2")
    @io.swagger.v3.oas.annotations.media.Schema(example = "ydist", required= true, description = "Source Type")
    private String loginid2;
    @JsonProperty("extcode2")
    @io.swagger.v3.oas.annotations.media.Schema(example = "7363fd", required= true, description = "Source Type")
    private String extcode2;
    @JsonProperty("products")
    private List<ProductsC2C> products = null;
    @JsonProperty("language1")
    @io.swagger.v3.oas.annotations.media.Schema(example = "0", required= true, description = "Source Type")
    private String language1;
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

    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }

    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }

    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }

    @JsonProperty("extrefnum")
    public String getExtrefnum() {
        return extrefnum;
    }

    @JsonProperty("extrefnum")
    public void setExtrefnum(String extrefnum) {
        this.extrefnum = extrefnum;
    }

    @JsonProperty("msisdn2")
    public String getMsisdn2() {
        return msisdn2;
    }

    @JsonProperty("msisdn2")
    public void setMsisdn2(String msisdn2) {
        this.msisdn2 = msisdn2;
    }

    @JsonProperty("loginid2")
    public String getLoginid2() {
        return loginid2;
    }

    @JsonProperty("loginid2")
    public void setLoginid2(String loginid2) {
        this.loginid2 = loginid2;
    }

    @JsonProperty("extcode2")
    public String getExtcode2() {
        return extcode2;
    }

    @JsonProperty("extcode2")
    public void setExtcode2(String extcode2) {
        this.extcode2 = extcode2;
    }

    @JsonProperty("products")
    public List<ProductsC2C> getProducts() {
        return products;
    }

    @JsonProperty("products")
    public void setProducts(List<ProductsC2C> products) {
        this.products = products;
    }

    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
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

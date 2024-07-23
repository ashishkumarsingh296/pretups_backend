package com.restapi.user.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class DataVcApp {

	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("extcode")
    private String extcode;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("loginid")
    private String loginid;
	
	
    @JsonProperty("language2")
    private String language2;
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("type")
    private String type;
    @JsonProperty("transferId")
    private String transferId;
    @JsonProperty("paymentinstcode")
    private String paymentinstcode;
    @JsonProperty("paymentinstdate")
    private String paymentinstdate;
    @JsonProperty("paymentinstnum")
    private String paymentinstnum;
    @JsonProperty("voucherDetails")
    private List<VoucherDetail> voucherDetails = null;
    
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("password")
    private String password;
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("pin")
    private String pin;
    
    
    @io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("msisdn")
    private String msisdn;
    
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("status")
    private String status;
  
    @io.swagger.v3.oas.annotations.media.Schema(example = "2341", required = true/* , defaultValue = "" */)
    @JsonProperty("extcode")
    public String getExtcode() {
        return extcode;
    }

    @JsonProperty("extcode")
    public void setExtcode(String extcode) {
        this.extcode = extcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
    @JsonProperty("loginid")
    public String getLoginid() {
        return loginid;
    }

    @JsonProperty("loginid")
    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "0", required = true/* , defaultValue = "" */)
    @JsonProperty("language2")
    public String getLanguage2() {
        return language2;
    }

    @JsonProperty("language2")
    public void setLanguage2(String language2) {
        this.language2 = language2;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "0", required = true/* , defaultValue = "" */)
    @JsonProperty("language1")
    public String getLanguage1() {
        return language1;
    }

    @JsonProperty("language1")
    public void setLanguage1(String language1) {
        this.language1 = language1;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "C2CVOUCHERAPPROVAL", required = true/* , defaultValue = "" */)
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "CT191219.0115.100001", required = true/* , defaultValue = "" */)
    @JsonProperty("transferId")
    public String getTransferId() {
        return transferId;
    }

    @JsonProperty("transferId")
    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "CASH", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentinstcode")
    public String getPaymentinstcode() {
        return paymentinstcode;
    }

    @JsonProperty("paymentinstcode")
    public void setPaymentinstcode(String paymentinstcode) {
        this.paymentinstcode = paymentinstcode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "13/01/20", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentinstdate")
    public String getPaymentinstdate() {
        return paymentinstdate;
    }

    @JsonProperty("paymentinstdate")
    public void setPaymentinstdate(String paymentinstdate) {
        this.paymentinstdate = paymentinstdate;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "536427", required = true/* , defaultValue = "" */)
    @JsonProperty("paymentinstnum")
    public String getPaymentinstnum() {
        return paymentinstnum;
    }

    @JsonProperty("paymentinstnum")
    public void setPaymentinstnum(String paymentinstnum) {
        this.paymentinstnum = paymentinstnum;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */)
    @JsonProperty("voucherDetails")
    public List<VoucherDetail> getVoucherDetails() {
        return voucherDetails;
    }

    @JsonProperty("voucherDetails")
    public void setVoucherDetails(List<VoucherDetail> voucherDetails) {
        this.voucherDetails = voucherDetails;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "2468", required = true/* , defaultValue = "" */)
    @JsonProperty("pin")
    public String getPin() {
        return pin;
    }

    @JsonProperty("pin")
    public void setPin(String pin) {
        this.pin = pin;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "723000000", required = true/* , defaultValue = "" */)
    @JsonProperty("msisdn")
    public String getMsisdn() {
        return msisdn;
    }

    @JsonProperty("msisdn")
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "Test Data", required = true/* , defaultValue = "" */)
    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */)
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("transferId = ").append(transferId)
        		.append("language2 = ").append( language2)
        		.append("language1").append( language1)
        		.append("type = ").append(type)
        		.append("paymentinstcode = ").append( paymentinstcode)
        		.append("paymentinstdate = ").append( paymentinstdate)
        		.append("paymentinstnum = ").append( paymentinstnum)
        		.append("voucherDetails = ").append( voucherDetails)
        		.append("remarks").append( remarks)
        		.append("status = ").append(status)
        		.append("extnwcode = ").append(extnwcode)
        		.append("msisdn = ").append(msisdn)
        		.append("pin = ").append(pin)
        		.append("loginid = ").append(loginid)
        		.append("password = ").append(password)
        		.append("extcode = ").append(extcode)).toString();
    }
    
}
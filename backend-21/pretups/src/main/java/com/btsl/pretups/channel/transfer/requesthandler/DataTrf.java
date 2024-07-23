package com.btsl.pretups.channel.transfer.requesthandler;

import com.fasterxml.jackson.annotation.JsonProperty;



public class DataTrf {

    @JsonProperty("extcode")
    private String extcode;
    @JsonProperty("loginid")
    private String loginid;
    
    @JsonProperty("extnwcode")
    private String extnwcode;
   
    @JsonProperty("transferId")
    private String transferId;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("networkCode")
    private String networkCode;
    @JsonProperty("networkCodeFor")
    private String networkCodeFor;
    

    @JsonProperty("password")
    private String password;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("msisdn")
    private String msisdn;
   
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "234", required = true/* , defaultValue = "" */)
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
    @JsonProperty("extnwcode")
    public String getExtnwcode() {
        return extnwcode;
    }

    @JsonProperty("extnwcode")
    public void setExtnwcode(String extnwcode) {
        this.extnwcode = extnwcode;
    }
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "CT200109.0609.100001", required = true/* , defaultValue = "" */)
    @JsonProperty("transferId")
    public String getTransferId() {
        return transferId;
    }

    @JsonProperty("transferId")
    public void setTransferId(String transferId) {
        this.transferId = transferId;
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
    @io.swagger.v3.oas.annotations.media.Schema(example = "T", required = true/* , defaultValue = "" */)
    @JsonProperty("transferType")
    public String getTransferType() {
        return transferType;
    }

    @JsonProperty("transferType")
    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
    @JsonProperty("networkCode")
    public String getNetworkCode() {
        return networkCode;
    }

    @JsonProperty("networkCode")
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }
    @io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */)
    @JsonProperty("networkCodeFor")
    public String getNetworkCodeFor() {
        return networkCodeFor;
    }

    @JsonProperty("networkCodeFor")
    public void setNetworkCodeFor(String networkCodeFor) {
        this.networkCodeFor = networkCodeFor;
    }
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("transferId = ").append(transferId)
        		.append("transferType = ").append( transferType).append("networkCode").append( networkCode)
        		.append("networkCodeFor = ").append( networkCodeFor)
        		.append("extnwcode = ").append(extnwcode)
        		.append("msisdn = ").append(msisdn)
        		.append("pin = ").append(pin)
        		.append("loginid = ").append(loginid)
        		.append("password = ").append(password)
        		.append("extcode = ").append(extcode)).toString();
    }
    
}


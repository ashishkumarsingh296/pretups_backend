package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Schema(description = "This is a data field")
public class ChannelTransferDetailsData {
	@JsonProperty("userType")
	@Setter
	@Getter
	private String userType;
	@JsonProperty("date")
    private String date;
	@JsonProperty("extnwcode")
    private String extnwcode;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("msisdn")
    private String msisdn;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("toMsisdn")
    private String toMsisdn;
	
	public String getToMsisdn() {
		return toMsisdn;
	}
	public void setToMsisdn(String toMsisdn) {
		this.toMsisdn = toMsisdn;
	}
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("pin")
    private String pin;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("loginid")
    private String loginid;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("password")
    private String password;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden=true)
    @JsonProperty("extcode")
    private String extcode;
	
	
    @JsonProperty("commissionProfileID")
    private String commissionProfileID;
    @JsonProperty("commissionProfileVersion")
    private String commissionProfileVersion;
    @JsonProperty("products")
    Products[] products;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("transferSubType")
    private String transferSubType;
    @JsonProperty("cbcflag")
    private String cbcflag;
    @JsonProperty("paymenttype")
    private String paymenttype;
    
    @io.swagger.v3.oas.annotations.media.Schema(example = "0", required= false, description= "Language1")
    @JsonProperty("language1")
    private String language1;
    @JsonProperty("dualCommission")
    private String dualCommission;
	

	@JsonProperty("dualCommission")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NC", required = true/* , defaultValue = "" */, description = "Dual Commission")
	public String getDualCommission() {
		return dualCommission;
	}
	@JsonProperty("dualCommission")
	public void setDualCommission(String dualCommission) {
		this.dualCommission = dualCommission;
	}
	@JsonProperty("date")
	@io.swagger.v3.oas.annotations.media.Schema(example = "01/01/20", required = false/* , defaultValue = "" */, description = "Date")
    public String getDate() {
		return date;
	}
	@JsonProperty("date")
	public void setDate(String date) {
		this.date = date;
	}
	@io.swagger.v3.oas.annotations.media.Schema(example = "1192", required = true, description = "Commission Profile Id")
	@JsonProperty("commissionProfileID")
	public String getCommissionProfileID() {
		return commissionProfileID;
	}
	@JsonProperty("commissionProfileID")
	public void setCommissionProfileID(String commissionProfileID) {
		this.commissionProfileID = commissionProfileID;
	}
	@JsonProperty("commissionProfileVersion")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */, description = "Commission Profile Version")
	public String getCommissionProfileVersion() {
		return commissionProfileVersion;
	}
	@JsonProperty("commissionProfileVersion")
	public void setCommissionProfileVersion(String commissionProfileVersion) {
		this.commissionProfileVersion = commissionProfileVersion;
	}
	@JsonProperty("products")
	public Products[] getProducts() {
		return products;
	}
	@JsonProperty("products")
	public void setProducts(Products[] products) {
		this.products = products;
	}
	@JsonProperty("transferType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "C2C", required = true/* , defaultValue = "" */, description = "Transfer Type")
	public String getTransferType() {
		return transferType;
	}
	@JsonProperty("transferType")
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	@JsonProperty("transferSubType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "T", required = true/* , defaultValue = "" */, description = "Transfer Sub Type")
	public String getTransferSubType() {
		return transferSubType;
	}
	@JsonProperty("transferSubType")
	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}
	@JsonProperty("cbcflag")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Y", required = true/* , defaultValue = "" */, description = "CBC Flag")
	public String isCbcflag() {
		return cbcflag;
	}
	@JsonProperty("cbcflag")
	public void setCbcflag(String cbcflag) {
		this.cbcflag = cbcflag;
	}

	@JsonProperty("language1")
    public String getLanguage1() {
		return language1;
	}
	@JsonProperty("language1")
	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	
    @JsonProperty("paymenttype")
    @io.swagger.v3.oas.annotations.media.Schema(example = "ALL", required = true/* , defaultValue = "" */, description = "Payment Type")
    public String getPaymenttype() {
		return paymenttype;
	}
    @JsonProperty("paymenttype")
	public void setPaymenttype(String paymenttype) {
		this.paymenttype = paymenttype;
	}
    /*@JsonIgnore
    private HashMap<String,Object> additionalProperties = new HashMap<String, Object>();*/


    @JsonProperty("extnwcode")
    @io.swagger.v3.oas.annotations.media.Schema(example = "JSON", required = true, defaultValue = "NG", description = "External Network Code")
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
	@Override
	public String toString() {
		return "ChannelTransferDetailsData [date=" + date + ", extnwcode=" + extnwcode + ", msisdn=" + msisdn + ", pin="
				+ pin + ", loginid=" + loginid + ", password=" + password + ", extcode=" + extcode
				+ ", commissionProfileID=" + commissionProfileID + ", commissionProfileVersion="
				+ commissionProfileVersion + ", products=" + Arrays.toString(products) + ", transferType="
				+ transferType + ", transferSubType=" + transferSubType + ", cbcflag=" + cbcflag + ", paymenttype="
				+ paymenttype + ", language1=" + language1 + ", dualCommission=" + dualCommission + "]";
	}


/*
    @JsonAnyGetter
    public HashMap<String,Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }*/
	


}

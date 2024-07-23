
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
    "msisdn2",
    "loginid2",
    "extcode2",
    "products",
    "refnumber",
    "paymentdetails",
    "remarks",
    "language1"
})
public class C2CStockInitiateData {

    @JsonProperty("date")
    private String date;
    @JsonProperty("extnwcode")
    private String extnwcode;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("loginid")
    private String loginid;
    @JsonProperty("password")
    private String password;
    @JsonProperty("extcode")
    private String extcode;
    @JsonProperty("msisdn2")
    private String msisdn2;
    @JsonProperty("loginid2")
    private String loginid2;
    @JsonProperty("extcode2")
    private String extcode2;
    @JsonProperty("products")
    private List<ProductsC2C> products = null;
    @JsonProperty("refnumber")
    private String refnumber;
    @JsonProperty("paymentdetails")
    private List<PaymentdetailC2C> paymentdetails = null;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("language1")
    private String language1;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    /*
     * Chnages for File upload functionality
     */
    @io.swagger.v3.oas.annotations.media.Schema(example = "jpg", required = true/* , position = 1 */, description="File Type(pdf, jpg, png")
    private String fileType;
    @io.swagger.v3.oas.annotations.media.Schema(example = "c2cBatchTransfer", required = true/* , position = 2 */, description="File Name")
    private String fileName;
    @io.swagger.v3.oas.annotations.media.Schema(example = "Base64 Encoded data", required = true/* , position = 3 */, description="Base64 Encoded File as String")
    private String fileAttachment;
    @io.swagger.v3.oas.annotations.media.Schema(example = "true", required = true/* , position = 4 */, description="boolean value (true/false)")
    private String fileUploaded;

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

    @JsonProperty("refnumber")
    public String getRefnumber() {
        return refnumber;
    }

    @JsonProperty("refnumber")
    public void setRefnumber(String refnumber) {
        this.refnumber = refnumber;
    }

    @JsonProperty("paymentdetails")
    public List<PaymentdetailC2C> getPaymentdetails() {
        return paymentdetails;
    }

    @JsonProperty("paymentdetails")
    public void setPaymentdetails(List<PaymentdetailC2C> paymentdetails) {
        this.paymentdetails = paymentdetails;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
    
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileAttachment() {
		return fileAttachment;
	}

	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}

	public String getFileUploaded() {
		return fileUploaded;
	}

	public void setFileUploaded(String fileUploaded) {
		this.fileUploaded = fileUploaded;
	}

	


}

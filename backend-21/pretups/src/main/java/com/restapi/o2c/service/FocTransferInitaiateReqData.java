package com.restapi.o2c.service;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;



public class FocTransferInitaiateReqData {
	
	
	@JsonProperty("refnumber")
    private String refnumber;
	
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "0/1", required = true, defaultValue = "language1")
	public String getLanguage1() {
		return language1;
	}
	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	
	@io.swagger.v3.oas.annotations.media.Schema(example = "0/1", required = true, defaultValue = "language2")
	public String getLanguage2() {
		return language2;
	}
	public void setLanguage2(String language2) {
		this.language2 = language2;
	}
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true, defaultValue = "focProducts")
	public ArrayList<FOCProduct> getFocProducts() {
		return focProducts;
	}
	public void setFocProducts(ArrayList<FOCProduct> focProducts) {
		this.focProducts = focProducts;
	}
	@JsonProperty("remarks")
    private String remarks;
	
	@JsonProperty("language1")
    private String language1;
	
	@JsonProperty("language2")
    private String language2;
	
	@JsonProperty("pin")
	private String pin;

	
	@JsonProperty("msisdn2")
	private String msisdn2;
     
	private ArrayList<FOCProduct> focProducts;
    

	@io.swagger.v3.oas.annotations.media.Schema(example = "msisdn2", required = true, defaultValue = "Msisdn2")
	public String getMsisdn2() {
		return msisdn2;
	}
	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}

	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true, defaultValue = "Pin")
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "345", required = true, defaultValue = "Reference Number")
	public String getRefnumber() {
		return refnumber;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FocTransferInitaiateReqData [refnumber=");
		builder.append(refnumber);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append(", language1=");
		builder.append(language1);
		builder.append(", language2=");
		builder.append(language2);
		builder.append(", pin=");
		builder.append(pin);
		builder.append(", msisdn2=");
		builder.append(msisdn2);
		builder.append(", focProducts=");
		builder.append(focProducts);
		builder.append("]");
		return builder.toString();
	}
	public void setRefnumber(String refnumber) {
		this.refnumber = refnumber;
	}
	
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true, defaultValue = "Remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	

	
} 




class FOCProduct {
	@io.swagger.v3.oas.annotations.media.Schema(example = "ETOPUP", required = true, description= "Product code")
    private String productCode;
	@io.swagger.v3.oas.annotations.media.Schema(example = "10", required = true,  description= "Approval quantity")
    private String appQuantity;
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getAppQuantity() {
		return appQuantity;
	}
	public void setAppQuantity(String appQuantity) {
		this.appQuantity = appQuantity;
	}
	
	@Override
	public String toString() {
		return "FOCProduct [productCode=" + productCode + ", appQuantity=" + appQuantity + "]";
	}
	
	
}



package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CInitiateOptReqVO extends OAuthUser{
	@JsonProperty("data")
    private List<O2CInitiateOptReqData> o2CInitiateOptReqData;

    public List<O2CInitiateOptReqData> getO2CInitiateOptReqData() {
        return o2CInitiateOptReqData;
    }

    public void setO2CInitiateOptReqData(List<O2CInitiateOptReqData> data) {
        this.o2CInitiateOptReqData = data;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("o2CInitiateOptReqData = ").append(o2CInitiateOptReqData)).toString();
    }
}

class O2CInitiateOptReqData {
	@JsonProperty("products")
    private List<Products> products = null;
	
	@JsonProperty("paymentdetails")
    private List<PaymentDetails> paymentdetails = null;
	
	@JsonProperty("msisdn")
    private String msisdn;
	
	@JsonProperty("pin")
    private String pin;
	
	@JsonProperty("refnumber")
    private String refnumber;
	
	@JsonProperty("remarks")
    private String remarks;
	
	@JsonProperty("language")
    private String language;
    
	public List<Products> getProducts() {
		return products;
	}
	public void setProducts(List<Products> products) {
		this.products = products;
	}
	public List<PaymentDetails> getPaymentdetails() {
		return paymentdetails;
	}
	public void setPaymentdetails(List<PaymentDetails> paymentdetails) {
		this.paymentdetails = paymentdetails;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getRefnumber() {
		return refnumber;
	}
	public void setRefnumber(String refnumber) {
		this.refnumber = refnumber;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("O2CInitiateOptReqData = ")
        		.append("products = ").append(products)
        		.append("paymentdetails = ").append(paymentdetails)
        		.append("msisdn = ").append(msisdn)
        		.append("pin = ").append(pin)
        		.append("refnumber = ").append(refnumber)
        		.append("language = ").append(language)
        		.append("remarks = ").append(remarks)
        		.append("language = ").append(language)).toString();
    }
} 


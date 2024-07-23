package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CInitiateRequestVO extends OAuthUser{
	@JsonProperty("data")
    private List<O2CInitiateReqData> o2CInitiateReqData;

    public List<O2CInitiateReqData> getO2CInitiateReqData() {
        return o2CInitiateReqData;
    }

    public void setO2CInitiateReqData(List<O2CInitiateReqData> data) {
        this.o2CInitiateReqData = data;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("o2CInitiateReqData = ").append(o2CInitiateReqData)).toString();
    }
}

class O2CInitiateReqData {
	@JsonProperty("products")
    private List<Products> products = null;
	
	@JsonProperty("paymentdetails")
    private List<PaymentDetailsO2C> paymentdetails = null;
	
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
	public List<PaymentDetailsO2C> getPaymentdetails() {
		return paymentdetails;
	}
	public void setPaymentdetails(List<PaymentDetailsO2C> paymentdetails) {
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
	
	@Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("o2CInitiateReqData = ")
        		.append("products = ").append(products)
        		.append("paymentdetails = ").append(paymentdetails)
        		.append("refnumber = ").append(refnumber)
        		.append("language = ").append(language)
        		.append("remarks = ").append(remarks)
        		.append("language = ").append(language)).toString();
    }
} 

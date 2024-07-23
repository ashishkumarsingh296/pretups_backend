package com.restapi.o2c.service;

import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CReturnRequestVO extends OAuthUser{
	@JsonProperty("data")
    private List<O2CReturnReqData> o2CReturnReqData;

    public List<O2CReturnReqData> getO2CReturnReqData() {
        return o2CReturnReqData;
    }

    public void setO2CReturnReqData(List<O2CReturnReqData> data) {
        this.o2CReturnReqData = data;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("o2CReturnReqData = ").append(o2CReturnReqData)).toString();
    }
}

class O2CReturnReqData {
	@JsonProperty("products")
    private List<Products> products = null;
	
	@JsonProperty("pin")
    private String pin;
	
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
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
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
        		.append("language = ").append(language)
        		.append("remarks = ").append(remarks)
        		.append("pin = ").append(pin)).toString();
    }
} 


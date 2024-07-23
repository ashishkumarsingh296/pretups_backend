package com.restapi.o2c.service;

import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CWithdrawlRequestVO extends OAuthUser{
	
	@JsonProperty("data")
    private List<O2CWithdrawData> o2CInitiateReqData;

	public List<O2CWithdrawData> getO2CInitiateReqData() {
		return o2CInitiateReqData;
	}

	public void setO2CInitiateReqData(List<O2CWithdrawData> o2cInitiateReqData) {
		o2CInitiateReqData = o2cInitiateReqData;
	}
}

class O2CWithdrawData 
{
	private String remarks;
	private String fromUserId;
	private List<Products> products = null;
	private String walletType;
	
	
	
	 public String getWalletType() {
		return walletType;
	}

	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}

	@JsonProperty("pin")
	 private String pin;
		
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}
	
	
	
	
}

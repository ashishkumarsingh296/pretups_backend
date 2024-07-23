package com.restapi.c2s.services;

public class UserBalanceVO {
	
	private String balance;
	private String productName;
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	private String productCode;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserBalanceVO [balance=");
		builder.append(balance);
		builder.append(", productCode=");
		builder.append(productCode);
		builder.append(", productName=");
		builder.append(productName);
		builder.append("]");
		return builder.toString();
	}

}

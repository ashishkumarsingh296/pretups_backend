package com.btsl.pretups.channel.transfer.businesslogic;

public class UserProductDetailsForO2C {
	private String productName;
	private String productShortName;
	private String productCode;
	private String productUserMinTransferValue;
	private String productUserMaxTransferValue;
	private String productUserBalance;	
	private String productShortCode;
	private String mrp;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductShortName() {
		return productShortName;
	}
	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductUserMinTransferValue() {
		return productUserMinTransferValue;
	}
	public void setProductUserMinTransferValue(String productUserMinTransferValue) {
		this.productUserMinTransferValue = productUserMinTransferValue;
	}
	public String getProductUserMaxTransferValue() {
		return productUserMaxTransferValue;
	}
	public void setProductUserMaxTransferValue(String productUserMaxTransferValue) {
		this.productUserMaxTransferValue = productUserMaxTransferValue;
	}
	public String getProductUserBalance() {
		return productUserBalance;
	}
	public void setProductUserBalance(String productUserBalance) {
		this.productUserBalance = productUserBalance;
	}
	public String getProductShortCode() {
		return productShortCode;
	}
	public void setProductShortCode(String productShortCode) {
		this.productShortCode = productShortCode;
	}
	public String getMrp() {
		return mrp;
	}
	public void setMrp(String mrp) {
		this.mrp = mrp;
	}
	@Override
	public String toString() {
		return "UserProductDetailsForO2C [productName=" + productName + ", productShortName=" + productShortName
				+ ", productCode=" + productCode + ", productUserMinTransferValue=" + productUserMinTransferValue
				+ ", productUserMaxTransferValue=" + productUserMaxTransferValue + ", productUserBalance="
				+ productUserBalance + ", productShortCode=" + productShortCode +  ", mrp=" + mrp + "]";
	}
	
	

}

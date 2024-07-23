package com.btsl.pretups.channel.transfer.businesslogic;

public class UserProductDetails {
	private String productName;
	private String productUserMinTransferValue;
	private String productUserMaxTransferValue;
	private String productUserBalance;	
	private String productShortCode;
	
	

	public String getProductShortCode() {
		return productShortCode;
	}

	public void setProductShortCode(String productShortCode) {
		this.productShortCode = productShortCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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

	@Override
	public String toString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("UserProductDetails [");
		sbf.append("productName=" + productName);
		sbf.append("productUserMinTransferValue=" + productUserMinTransferValue);
		sbf.append("productUserMaxTransferValue=" + productUserMaxTransferValue);
		sbf.append("productUserBalance=" + productUserBalance);
		sbf.append("]");
		return sbf.toString();
	}
}

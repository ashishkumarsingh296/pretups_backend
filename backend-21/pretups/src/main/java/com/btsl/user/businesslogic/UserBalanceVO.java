package com.btsl.user.businesslogic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Get ChannelUser model
 * 
 * @author VENKATESAN.S
 */

@Getter
@Setter
public class UserBalanceVO implements Serializable{

	/**
	 * 
	 */
	private String balance;
	private String productName;
	private long productShortCode;
	private String productCode;
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
	public long getProductShortCode() {
		return productShortCode;
	}
	public void setProductShortCode(long productShortCode) {
		this.productShortCode = productShortCode;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	
	
}

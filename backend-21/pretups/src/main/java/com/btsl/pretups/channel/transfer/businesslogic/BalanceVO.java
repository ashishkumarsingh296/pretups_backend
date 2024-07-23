package com.btsl.pretups.channel.transfer.businesslogic;

public class BalanceVO {

		String productName;
		Double balance;
		
		
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public Double getBalance() {
			return balance;
		}
		public void setBalance(Double balance) {
			this.balance = balance;
		}
		@Override
		public String toString() {
			return "BalanceVO [productName=" + productName + ", balance=" + balance + "]";
		}
		
		
	}



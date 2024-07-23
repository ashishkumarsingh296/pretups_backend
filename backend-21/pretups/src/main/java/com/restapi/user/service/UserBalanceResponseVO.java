package com.restapi.user.service;

import com.btsl.common.BaseResponse;

public class UserBalanceResponseVO extends BaseResponse {
	
	String openingBalance;
	String closingBalance;
	
	public String getOpeningBalance() {
		return openingBalance;
	}
	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}
	public String getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(String closingBalance) {
		this.closingBalance = closingBalance;
	}
	@Override
	public String toString() {
		return "UserBalanceResponseVO [openingBalance=" + openingBalance + ", closingBalance=" + closingBalance + "]";
	}
	
	

}

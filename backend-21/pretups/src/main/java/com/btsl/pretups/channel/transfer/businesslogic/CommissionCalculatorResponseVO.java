package com.btsl.pretups.channel.transfer.businesslogic;

public class CommissionCalculatorResponseVO {

	String currentMonthIncome;
	
	String previousMonthIncome;
	
	String previousMonthFromDate;
	
	String previousMonthToDate;
	
	String currentFromDate;
	
	String currentToDate;
	
	public String getCurrentFromDate() {
		return currentFromDate;
	}

	public void setCurrentFromDate(String currentFromDate) {
		this.currentFromDate = currentFromDate;
	}

	public String getCurrentToDate() {
		return currentToDate;
	}

	public void setCurrentToDate(String currentToDate) {
		this.currentToDate = currentToDate;
	}

	public String getCurrentMonthIncome() {
		return currentMonthIncome;
	}

	public void setCurrentMonthIncome(String currentMonthIncome) {
		this.currentMonthIncome = currentMonthIncome;
	}

	public String getPreviousMonthIncome() {
		return previousMonthIncome;
	}

	public void setPreviousMonthIncome(String previousMonthIncome) {
		this.previousMonthIncome = previousMonthIncome;
	}
	

	public String getPreviousMonthFromDate() {
		return previousMonthFromDate;
	}

	public void setPreviousMonthFromDate(String previousMonthFromDate) {
		this.previousMonthFromDate = previousMonthFromDate;
	}

	public String getPreviousMonthToDate() {
		return previousMonthToDate;
	}

	public void setPreviousMonthToDate(String previousMonthToDate) {
		this.previousMonthToDate = previousMonthToDate;
	}
	
	
	@Override
	public String toString() {
		return "CommissionCalculatorResponseVO [currentMonthIncome=" + currentMonthIncome + ", previousMonthIncome="
				+ previousMonthIncome + ", previousMonthFromDate=" + previousMonthFromDate + ", previousMonthToDate="
				+ previousMonthToDate + ", currentFromDate=" + currentFromDate + ", currentToDate=" + currentToDate
				+ "]";
	}
	
	
	
}

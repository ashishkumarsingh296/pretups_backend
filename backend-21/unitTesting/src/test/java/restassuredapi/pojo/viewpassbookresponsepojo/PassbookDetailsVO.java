package restassuredapi.pojo.viewpassbookresponsepojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PassbookDetailsVO {
	
	
	public long closingBalance;
	@JsonIgnore
	private Date fromDate = null;
	@JsonIgnore
	 private Date toDate = null;
	 private long commissionValue;
	 private long c2SStockSales;
	private long c2CStockSales;
	@JsonIgnore
	 private Date transferDate; 
	private long withdrawBalance;
	private long returnBalance;
	private long stockPurchase;
	@JsonIgnore
	private String userID;
	private long marginAmount;
	public long openingBalance;
	 public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public long getOpeningBalance() {
		return openingBalance;
	}
	public void setOpeningBalance(long openingBalance) {
		this.openingBalance = openingBalance;
	}
	public long getClosingBalance() {
		return closingBalance;
	}
	public void setClosingBalance(long closingBalance) {
		this.closingBalance = closingBalance;
	}
	public long getCommissionValue() {
		return commissionValue;
	}
	public void setCommissionValue(long commissionValue) {
		this.commissionValue = commissionValue;
	}
	public long getC2SStockSales() {
		return c2SStockSales;
	}
	public void setC2SStockSales(long c2sStockSales) {
		c2SStockSales = c2sStockSales;
	}
	public long getC2CStockSales() {
		return c2CStockSales;
	}
	public void setC2CStockSales(long c2cStockSales) {
		c2CStockSales = c2cStockSales;
	}
	public Date getTransferDate() {
		return transferDate;
	}
	public void setTransferDate(Date transferDate) {
		this.transferDate = transferDate;
	}
	public long getWithdrawBalance() {
		return withdrawBalance;
	}
	public void setWithdrawBalance(long withdrawBalance) {
		this.withdrawBalance = withdrawBalance;
	}
	public long getReturnBalance() {
		return returnBalance;
	}
	public void setReturnBalance(long returnBalance) {
		this.returnBalance = returnBalance;
	}
	public long getStockPurchase() {
		return stockPurchase;
	}
	public void setStockPurchase(long stockPurchase) {
		this.stockPurchase = stockPurchase;
	}
	
	public long getMarginAmount() {
		return marginAmount;
	}
	public void setMarginAmount(long marginAmount) {
		this.marginAmount = marginAmount;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	

}
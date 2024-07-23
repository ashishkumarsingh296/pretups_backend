package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class C2STransactionDetails {
	private Date transferdate;
	private String transferCount;
	private String transferValue;
	private String transferDateString;
	
	@JsonIgnore
	private String amount;
	
	public Date getTransferdate() {
		return transferdate;
	}
	public void setTransferdate(Date transferdate) {
		this.transferdate = transferdate;
	}
	public String getTransferCount() {
		return transferCount;
	}
	public void setTransferCount(String transferCount) {
		this.transferCount = transferCount;
	}
	public String getTransferValue() {
		return transferValue;
	}
	public void setTransferValue(String transferValue) {
		this.transferValue = transferValue;
	}
	
	public String getTransferDateString() {
		return transferDateString;
	}
	public void setTransferDateString(String transferDateString) {
		this.transferDateString = transferDateString;
	}
	@JsonIgnore
	public String getAmount() {
		return amount;
	}
	@JsonIgnore
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
}

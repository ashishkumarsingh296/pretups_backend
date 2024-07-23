package com.restapi.c2s.services;

import java.util.List;

public class TxnIDBaseResponse {
	private String row;
	private String transactionID;
	private String transactionDateTime;
	private String message;
	private String profileID;
	private List<String> voucherList;
	private String profileName;
	
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getProfileID() {
		return profileID;
	}
	public void setProfileID(String profileID) {
		this.profileID = profileID;
	}
	
	public List<String> getVoucherList() {
		return voucherList;
	}
	public void setVoucherList(List<String> voucherList) {
		this.voucherList = voucherList;
	}

	public String getTransactionDateTime() {
		return transactionDateTime;
	}
	public void setTransactionDateTime(String transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}
	
	@Override
	public String toString() {
		return "TxnIDBaseResponse [row=" + row + ", transactionID=" + transactionID + ", message=" + message
				+ ", profileID=" + profileID + ", voucherList=" + voucherList + ", transactionDateTime=" + transactionDateTime + "]";
	}
	
	

}

package com.btsl.pretups.processes.clientprocesses;

import java.util.Date;

public class ZBFnFVO {
	
	private String msisdn1;
	private String msisdn2;
	private String type;
	private Date expiryDate;
	private String dateString;
	private String operation;
	private String remarks;
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getMsisdn1() {
		return msisdn1;
	}
	public void setMsisdn1(String msisdn1) {
		this.msisdn1 = msisdn1;
	}
	public String getMsisdn2() {
		return msisdn2;
	}
	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getDateString() {
		return dateString;
	}
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

}

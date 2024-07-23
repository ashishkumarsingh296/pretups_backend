package com.btsl.pretups.channel.transfer.businesslogic;


public class C2CTrfReqMessage {	
	 public String getPaymenttype() {
		return paymenttype;
	}
	public void setPaymenttype(String paymenttype) {
		this.paymenttype = paymenttype;
	}
	
	public String getDualCommission() {
		return dualCommission;
	}
	public void setDualCommission(String dualCommission) {
		this.dualCommission = dualCommission;
	}
	public String getCommissionProfileID() {
		return commissionProfileID;
	}
	public void setCommissionProfileID(String commissionProfileID) {
		this.commissionProfileID = commissionProfileID;
	}
	public String getCommissionProfileVersion() {
		return commissionProfileVersion;
	}
	public void setCommissionProfileVersion(String commissionProfileVersion) {
		this.commissionProfileVersion = commissionProfileVersion;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	
	public String getTransferSubType() {
		return transferSubType;
	}
	public void setTransferSubType(String transferSubType) {
		this.transferSubType = transferSubType;
	}
	private String date;	
	 private String extnwcode;	
	 private String msisdn;	
	 private String extcode;	
	 private String loginid;	
	 private String password;	
	 private String txnid;	
	 private String commissionProfileID;
	 private String commissionProfileVersion;
	 private String transferType;
	 private String paymenttype;
	 private String transferSubType;
	 private String cbcflag;
	 //Added for file upload functionality
	 private String fileType;
	 private String fileName;
	 private String fileAttachment;
	 private String fileUploaded;
	 
	 public String getCbcflag() {
		return cbcflag;
	}
	public void setCbcflag(String cbcflag) {
		this.cbcflag = cbcflag;
	}
	private String dualCommission;
	 Products[] products;
	 PaymentDetails[] paymentdetails;
	 
	 public PaymentDetails[] getPaymentdetails() {
		return paymentdetails;
	}
	public void setPaymentdetails(PaymentDetails[] paymentdetails) {
		this.paymentdetails = paymentdetails;
	}
	public Products[] getProducts() {	
		return products;	
	 }	
	 public void setProducts(Products[] products) {	
		this.products = products;	
	 }	
	 private String pin;	
	 private String currentstatus;	
	 private String status;	
	 private String remarks;	
	 private String language1;	
	 // Getter Methods 	
	 public String getDate() {	
	  return date;	
	 }	
	 public String getExtnwcode() {	
	  return extnwcode;	
	 }	
	 public String getMsisdn() {	
	  return msisdn;	
	 }	
	 public String getExtcode() {	
	  return extcode;	
	 }	
	 public String getLoginid() {	
	  return loginid;	
	 }	
	 public String getPassword() {	
	  return password;	
	 }	
	 public String getTxnid() {	
	  return txnid;	
	 }	
	 public String getPin() {	
	  return pin;	
	 }	
	 public String getCurrentstatus() {	
	  return currentstatus;	
	 }	
	 public String getStatus() {	
	  return status;	
	 }	
	 public String getRemarks() {	
	  return remarks;	
	 }	
	 public String getLanguage1() {	
	  return language1;	
	 }	
	 // Setter Methods 	
	 public void setDate(String date) {	
	  this.date = date;	
	 }	
	 public void setExtnwcode(String extnwcode) {	
	  this.extnwcode = extnwcode;	
	 }	
	 public void setMsisdn(String msisdn) {	
	  this.msisdn = msisdn;	
	 }	
	 public void setExtcode(String extcode) {	
	  this.extcode = extcode;	
	 }	
	 public void setLoginid(String loginid) {	
	  this.loginid = loginid;	
	 }	
	 public void setPassword(String password) {	
	  this.password = password;	
	 }	
	 public void setTxnid(String txnid) {	
	  this.txnid = txnid;	
	 }	
	 public void setPin(String pin) {	
	  this.pin = pin;	
	 }	
	 public void setCurrentstatus(String currentstatus) {	
	  this.currentstatus = currentstatus;	
	 }	
	 public void setStatus(String status) {	
	  this.status = status;	
	 }	
	 public void setRemarks(String remarks) {	
	  this.remarks = remarks;	
	 }	
	 public void setLanguage1(String language1) {	
	  this.language1 = language1;	
	 }
	 
	 //
	 

	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();
		strBuild.append("C2CTrfReqMessage [");
		strBuild.append("date = " + date);
		strBuild.append("extnwcode = " + extnwcode);
		strBuild.append("msisdn = " + msisdn);
		strBuild.append("extcode = " + extcode);
		strBuild.append("loginid = " + loginid);
		strBuild.append("password = " + password);
		strBuild.append("txnid = " + txnid);
		strBuild.append("products = " + products);
		strBuild.append("pin = " + pin);
		strBuild.append("currentstatus = " + currentstatus);
		strBuild.append("status = " + status);
		strBuild.append("remarks = " + remarks);
		strBuild.append("language1 = " + language1);
		strBuild.append("fileType = " + fileType);
		strBuild.append("fileName = " + fileName);
		strBuild.append("fileAttachment = " + fileAttachment);
		strBuild.append("fileUploaded = " + fileUploaded);
		strBuild.append("]");
		return strBuild.toString();
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public String getFileUploaded() {
		return fileUploaded;
	}
	public void setFileUploaded(String fileUploaded) {
		this.fileUploaded = fileUploaded;
	}
}
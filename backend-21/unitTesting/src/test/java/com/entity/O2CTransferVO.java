package com.entity;

import com.commons.SystemPreferences;
import com.dbrepository.DBHandler;

public class O2CTransferVO {
	String mobilenumber, loginid, usercategory, usergrade, externalTxnNo, paymenttype, externalTxnDate, externalCode,
	quantity, remarks, language1, language2, smspin, domain, product;

	public O2CTransferVO(String domain, String category, String msisdn, String product){
		mobilenumber = msisdn;
		externalTxnNo = "CASH";
		externalTxnDate = DBHandler.AccessHandler.getCurrentServerDate(SystemPreferences.DATE_FORMAT_CAL_JAVA);
		externalCode = DBHandler.AccessHandler.getdetailsfromUsersTable(msisdn, "EXTERNAL_CODE")[0];
		quantity = "10";
		remarks = "Automation remarks";
		language1 = "Deafult language";
		language2 = "Second language";
		this.domain = domain;
		this.product = product;
	}
	
	
	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getUsercategory() {
		return usercategory;
	}

	public void setUsercategory(String usercategory) {
		this.usercategory = usercategory;
	}

	public String getUsergrade() {
		return usergrade;
	}

	public void setUsergrade(String usergrade) {
		this.usergrade = usergrade;
	}

	public String getExternalTxnNo() {
		return externalTxnNo;
	}

	public void setExternalTxnNo(String externalTxnNo) {
		this.externalTxnNo = externalTxnNo;
	}

	public String getPaymenttype() {
		return paymenttype;
	}

	public void setPaymenttype(String paymenttype) {
		this.paymenttype = paymenttype;
	}

	public String getExternalTxnDate() {
		return externalTxnDate;
	}

	public void setExternalTxnDate(String externalTxnDate) {
		this.externalTxnDate = externalTxnDate;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public String getLanguage1() {
		return language1;
	}


	public void setLanguage1(String language1) {
		this.language1 = language1;
	}


	public String getLanguage2() {
		return language2;
	}


	public void setLanguage2(String language2) {
		this.language2 = language2;
	}


	public String getSmspin() {
		return smspin;
	}


	public void setSmspin(String smspin) {
		this.smspin = smspin;
	}


	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}


	public String getProduct() {
		return product;
	}


	public void setProduct(String product) {
		this.product = product;
	}

	
	
}

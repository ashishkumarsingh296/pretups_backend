package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.annotation.JsonProperty;



public class DvdDetails extends OAuthUserData {
	
	@JsonProperty("date")
	@io.swagger.v3.oas.annotations.media.Schema(example = "11/05/20"/* , defaultValue = "" */, description="Date")
	private String date;
	
	@JsonProperty("language1")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0"/* , defaultValue = "" */, description ="Language1")
	private String language1;
	
	@JsonProperty("language2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0"/* , defaultValue = "" */, description ="Language2")
	private String language2;
	
	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description="External Network Code")
	private String extnwcode;
	
	@JsonProperty("extrefnum")
	@io.swagger.v3.oas.annotations.media.Schema(example = "67377"/* , defaultValue = "" */, description="External Reference Number")
	private String extrefnum;
	
	@JsonProperty("msisdn2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "7225739441", required = true/* , defaultValue = "" */, description="Msisdn2")
	private String msisdn2;
	
	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	private String pin;
	
	@JsonProperty("selector")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1", required = true/* , defaultValue = "" */, description ="Selector")
	private String selector;
	
	@JsonProperty("sendSms")
	@io.swagger.v3.oas.annotations.media.Schema(example = "N"/* , defaultValue = "" */, description ="Send voucher details via SMS or not, Y/N")
	private String sendSms;
	
	@JsonProperty("vouchertype")
	@io.swagger.v3.oas.annotations.media.Schema(example = "digital", required = true/* , defaultValue = "" */, description ="Voucher Type")
	private String vouchertype;
	
	@JsonProperty("vouchersegment")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NL", required = true/* , defaultValue = "" */, description ="Voucher segment")
	private String vouchersegment;
	
	@JsonProperty("voucherprofile")
	@io.swagger.v3.oas.annotations.media.Schema(example = "6", required = true/* , defaultValue = "" */, description ="Voucher Profile")
	private String voucherprofile;
	
	@JsonProperty("amount")
	@io.swagger.v3.oas.annotations.media.Schema(example = "100", required = true/* , defaultValue = "" */, description ="Denomination")
	private String amount;
	
	@JsonProperty("quantity")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2", required = true/* , defaultValue = "" */, description ="Quantity")
	private String quantity;
	
	@JsonProperty("rowSize")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2", required = false, description ="No. of rows")
	private Integer rowSize;
	
	@JsonProperty("rowCount")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2", required = false, description ="Row Count")
	private Integer rowCount;

	public Integer getRowSize() {
		return rowSize;
	}

	public void setRowSize(Integer rowSize) {
		this.rowSize = rowSize;
	}

	public Integer getRowCount() {
		return rowCount;
	}

	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
	}

	public String getVouchertype() {
		return vouchertype;
	}

	public void setVouchertype(String vouchertype) {
		this.vouchertype = vouchertype;
	}

	public String getVouchersegment() {
		return vouchersegment;
	}

	public void setVouchersegment(String vouchersegment) {
		this.vouchersegment = vouchersegment;
	}

	public String getVoucherprofile() {
		return voucherprofile;
	}

	public void setVoucherprofile(String voucherprofile) {
		this.voucherprofile = voucherprofile;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	public String getExtrefnum() {
		return extrefnum;
	}

	public void setExtrefnum(String extrefnum) {
		this.extrefnum = extrefnum;
	}

	public String getMsisdn2() {
		return msisdn2;
	}

	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public String getSendSms() {
		return sendSms;
	}

	public void setSendSms(String sendSms) {
		this.sendSms = sendSms;
	}

	@Override
	public String toString() {
		return "DvdDetails [date=" + date + ", language1=" + language1 + ", language2=" + language2 + ", extnwcode="
				+ extnwcode + ", extrefnum=" + extrefnum + ", msisdn2=" + msisdn2 + ", pin=" + pin + ", selector="
				+ selector + ", vouchertype=" + vouchertype + ", vouchersegment=" + vouchersegment + ", voucherprofile="
				+ voucherprofile + ", amount=" + amount + ", quantity=" + quantity + ", rowSize=" + rowSize
				+ ", rowCount=" + rowCount + ", sendSms=" + sendSms + "]";
	}
	
	
	
}


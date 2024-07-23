package com.restapi.c2s.services;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;



public class DvdSwaggRequestVO{
	
	@JsonProperty("date")
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
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
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
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
	
	@JsonProperty("voucherDetails")
	@io.swagger.v3.oas.annotations.media.Schema( required = true, description ="Voucher Details")
	private List<DvdSwaggVoucherDetails> voucherDetails = null;

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

	public List<DvdSwaggVoucherDetails> getVoucherDetails() {
		return voucherDetails;
	}

	public void setVoucherDetails(List<DvdSwaggVoucherDetails> voucherDetails) {
		this.voucherDetails = voucherDetails;
	}
	
	public String getSendSms() {
		return sendSms;
	}

	public void setSendSms(String sendSms) {
		this.sendSms = sendSms;
	}

	@Override
	public String toString() {
		return "DvdSwaggRequestVO [date=" + date + ", language1=" + language1 + ", language2=" + language2
				+ ", extnwcode=" + extnwcode + ", extrefnum=" + extrefnum + ", msisdn2=" + msisdn2 + ", pin=" + pin
				+ ", selector=" + selector + ", voucherDetails=" + voucherDetails + ", sendSms=" + sendSms + "]";
	}
	 
	 

}

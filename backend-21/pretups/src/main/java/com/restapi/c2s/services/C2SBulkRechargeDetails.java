package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUserData;
import com.fasterxml.jackson.annotation.JsonProperty;



public class C2SBulkRechargeDetails extends OAuthUserData{
	
	@JsonProperty("extnwcode")
	private String extnwcode;
	
	//@JsonProperty("msisdn")
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String msisdn;
	
	@JsonProperty("pin")//@JsonProperty("pin")
	private String pin;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("loginid")
	private String loginid;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("password")
	private String password;
	
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)//@JsonProperty("extcode")
	private String extcode;
	
	@JsonProperty("file")
	private String file;
	
	@JsonProperty("fileName")
	private String fileName;
	
	@JsonProperty("fileType")
	private String fileType;
	
	@JsonProperty("batchType")
	private String batchType;
	
	@JsonProperty("scheduleNow")
	private String scheduleNow;
	
	@JsonProperty("scheduleDate")
	private String scheduleDate;
	
	@JsonProperty("occurence")
	private String occurence;

	@JsonProperty("noOfDays")
	private String noOfDays;
	
	@JsonProperty("extnwcode")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NG", required = true/* , defaultValue = "" */, description="External Network Code")
	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}
	
	
	//@JsonProperty("msisdn")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "726576538", required = true/* , defaultValue = "" */)
	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	//@JsonProperty("loginid")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true/* , defaultValue = "" */)
	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	
	//@JsonProperty("password")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true/* , defaultValue = "" */)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	//@JsonProperty("extcode")
	//@io.swagger.v3.oas.annotations.media.Schema(example = "22435", required = true/* , defaultValue = "" */)
	public String getExtcode() {
		return extcode;
	}

	public void setExtcode(String extcode) {
		this.extcode = extcode;
	}

	@JsonProperty("file")
	@io.swagger.v3.oas.annotations.media.Schema(example = "", required = true/* , defaultValue = "" */, description="file")
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	@JsonProperty("scheduleNow")
	@io.swagger.v3.oas.annotations.media.Schema(example = "on", required = true/* , defaultValue = "" */, description= "scheduleNow")
	public String getScheduleNow() {
		return scheduleNow;
	}

	public void setScheduleNow(String scheduleNow) {
		this.scheduleNow = scheduleNow;
	}

	@JsonProperty("scheduleDate")
	@io.swagger.v3.oas.annotations.media.Schema(example = "30/08/20"/* , defaultValue = "" */,description ="scheduleDate",required = true)
	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}


	@JsonProperty("occurence")
	@io.swagger.v3.oas.annotations.media.Schema(example = "Daily"/* , defaultValue = "" */,description ="occurence",required = true)
	public String getOccurence() {
		return occurence;
	}

	public void setOccurence(String occurence) {
		this.occurence = occurence;
	}

	
	@JsonProperty("noOfDays")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2"/* , defaultValue = "" */,description ="noOfDays",required = true)
	public String getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(String noOfDays) {
		this.noOfDays = noOfDays;
	}

	
	@JsonProperty("batchType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "normal"/* , defaultValue = "" */,description ="batchType",required = true)
	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	@JsonProperty("fileName")
	@io.swagger.v3.oas.annotations.media.Schema(example = "SCH00001"/* , defaultValue = "" */,description ="fileName",required = true)
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@JsonProperty("fileType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "CSV"/* , defaultValue = "" */,description ="fileType",required = true)
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	@Override
	public String toString() {
		return "C2SBulkRechargeDetails [extnwcode=" + extnwcode + ", msisdn="
				+ msisdn + ", pin=" + pin + ", loginid=" + loginid
				+ ", password=" + password + ", extcode=" + extcode
				+ ", file=" + file + ", fileName="
				+ fileName + ", fileType=" + fileType + ", batchType="
				+ batchType + ", scheduleNow=" + scheduleNow
				+ ", scheduleDate=" + scheduleDate + ", occurence=" + occurence
				+ ", noOfDays=" + noOfDays + "]";
	}

	
	
	
	
}
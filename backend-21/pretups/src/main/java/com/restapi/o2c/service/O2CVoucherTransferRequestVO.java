package com.restapi.o2c.service;



import java.util.List;

import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CVoucherTransferRequestVO extends OAuthUser{
	@JsonProperty("data")
    private O2CVoucherTransferReqData o2CTrasfereReqData;

	public O2CVoucherTransferReqData getO2CTrasfereReqData() {
		return o2CTrasfereReqData;
	}

	public void setO2CTrasfereReqData(O2CVoucherTransferReqData o2cTrasfereReqData) {
		o2CTrasfereReqData = o2cTrasfereReqData;
	}

	@Override
	public String toString() {
		return "O2CVoucherTransferRequestVO [o2CTrasfereReqData="
				+ o2CTrasfereReqData + "]";
	}

	
}

class O2CVoucherTransferReqData {
	
	@JsonProperty("msisdn2")
    private String msisdn2;
	
	 @JsonProperty("loginid2")
     private String loginid2;
     
	 @JsonProperty("extcode2")
     private String extcode2;
     
	@JsonProperty("voucherDetails")
    private List<VoucherDetails> voucherDetails = null;
	
	@JsonProperty("paymentDetails")
	private List<PaymentDetailsO2C> paymentDetails = null;
	
	@JsonProperty("pin")
    private String pin;
	
	@JsonProperty("refnumber")
    private String refnumber;
	
	@JsonProperty("remarks")
    private String remarks;
	
	@JsonProperty("language")
    private String language;
    
	@JsonProperty("msisdn2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "msisdn2", required = true, defaultValue = "Msisdn2")
	public String getMsisdn2() {
		return msisdn2;
	}
	public void setMsisdn2(String msisdn2) {
		this.msisdn2 = msisdn2;
	}
	
	@JsonProperty("loginid2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "deepadist", required = true, defaultValue = "Loginid2")
	public String getLoginid2() {
		return loginid2;
	}
	public void setLoginid2(String loginid2) {
		this.loginid2 = loginid2;
	}
	
	@JsonProperty("extcode2")
	@io.swagger.v3.oas.annotations.media.Schema(example = "98765", required = true, defaultValue = "Extcode2")
	public String getExtcode2() {
		return extcode2;
	}
	public void setExtcode2(String extcode2) {
		this.extcode2 = extcode2;
	}
	public List<VoucherDetails> getVoucherDetails() {
		return voucherDetails;
	}
	public void setVoucherDetails(List<VoucherDetails> voucherDetails) {
		this.voucherDetails = voucherDetails;
	}
	public List<PaymentDetailsO2C> getPaymentDetails() {
		return paymentDetails;
	}
	public void setPaymentDetails(List<PaymentDetailsO2C> paymentdetails) {
		this.paymentDetails = paymentdetails;
	}
	
	@JsonProperty("pin")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true, defaultValue = "Pin")
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	@JsonProperty("refnumber")
	@io.swagger.v3.oas.annotations.media.Schema(example = "345", required = true, defaultValue = "Reference Number")
	public String getRefnumber() {
		return refnumber;
	}
	public void setRefnumber(String refnumber) {
		this.refnumber = refnumber;
	}
	
	@JsonProperty("remarks")
	@io.swagger.v3.oas.annotations.media.Schema(example = "1357", required = true, defaultValue = "Remarks")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	@JsonProperty("language")
	@io.swagger.v3.oas.annotations.media.Schema(example = "0", required = true, defaultValue = "language")
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	@Override
	public String toString() {
		return "O2CVoucherTransferReqData [msisdn2=" + msisdn2 + ", loginid2="
				+ loginid2 + ", extcode2=" + extcode2 + ", voucherDetails="
				+ voucherDetails + ", paymentDetails=" + paymentDetails
				+ ", pin=" + pin + ", refnumber=" + refnumber + ", remarks="
				+ remarks + ", language=" + language + "]";
	}
	
	
} 


class VoucherDetails {
	@JsonProperty("denomination")
    private String denomination = null;
	
	@JsonProperty("fromSerialNo")
    private String fromSerialNo = null;
	
	@JsonProperty("toSerialNo")
    private String toSerialNo = null;
	
	@JsonProperty("voucherType")
    private String voucherType = null;

	@JsonProperty("vouchersegment")
    private String vouchersegment = null;

	@JsonProperty("denomination")
	@io.swagger.v3.oas.annotations.media.Schema(example = "100", required = true, defaultValue = "Denomination")
	public String getDenomination() {
		return denomination;
	}
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	@JsonProperty("fromSerialNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "92123456788", required = true, defaultValue = "From Serial Number")
	public String getFromSerialNo() {
		return fromSerialNo;
	}

	public void setFromSerialNo(String fromSerialNo) {
		this.fromSerialNo = fromSerialNo;
	}

	@JsonProperty("toSerialNo")
	@io.swagger.v3.oas.annotations.media.Schema(example = "92123456789", required = true, defaultValue = "To Serial Number")
	public String getToSerialNo() {
		return toSerialNo;
	}

	public void setToSerialNo(String toSerialNo) {
		this.toSerialNo = toSerialNo;
	}

	@JsonProperty("voucherType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "digital", required = true, defaultValue = "Voucher Type")
	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	@JsonProperty("vouchersegment")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NL", required = true, defaultValue = "Voucher Segment")
	public String getVouchersegment() {
		return vouchersegment;
	}

	public void setVouchersegment(String vouchersegment) {
		this.vouchersegment = vouchersegment;
	}
	@Override
	public String toString() {
		return "VoucherDetails [denomination=" + denomination
				+ ", fromSerialNo=" + fromSerialNo + ", toSerialNo="
				+ toSerialNo + ", voucherType=" + voucherType
				+ ", vouchersegment=" + vouchersegment + "]";
	}

	
	
}
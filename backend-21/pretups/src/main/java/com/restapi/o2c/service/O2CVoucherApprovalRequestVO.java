package com.restapi.o2c.service;

import java.util.List;

import com.btsl.pretups.channel.transfer.businesslogic.Products;
import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class O2CVoucherApprovalRequestVO extends OAuthUser{
	
	@JsonProperty("data")
    private List<O2CVoucherApprvData> o2CInitiateReqData;

	public List<O2CVoucherApprvData> getO2CInitiateReqData() {
		return o2CInitiateReqData;
	}

	public void setO2CInitiateReqData(List<O2CVoucherApprvData> o2cInitiateReqData) {
		o2CInitiateReqData = o2cInitiateReqData;
	}
}

class O2CVoucherApprvData 
{	@JsonProperty("toUserId")
	private String toUserId;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("externalTxnNum")
	private String externalTxnNum;
	
	@JsonProperty("externalTxnDate")
	private String externalTxnDate;
	
	@JsonProperty("transferDate")
	private String transferDate;
	
	
	
	public String getExternalTxnNum() {
		return externalTxnNum;
	}

	public void setExternalTxnNum(String externalTxnNum) {
		this.externalTxnNum = externalTxnNum;
	}

	
		
	@JsonProperty("transactionId")
	private String transactionId;
	
	@JsonProperty("approvalLevel")
	private String approvalLevel;
	
	@JsonProperty("refrenceNumber")
	private String refrenceNumber;
	
	@JsonProperty("status")
	private String status;
	
	/*@JsonProperty("pin")
	 private String pin;*/
	
	@JsonProperty("voucherDetails")
    private List<VoucherDetailsApprv> voucherDetails = null;
	
	@JsonProperty("paymentDetails")
	private List<PaymentDetailsO2C> paymentDetails = null;
	
	

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}



	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getApprovalLevel() {
		return approvalLevel;
	}

	public void setApprovalLevel(String approvalLevel) {
		this.approvalLevel = approvalLevel;
	}

	/*public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
*/
	public List<VoucherDetailsApprv> getVoucherDetails() {
		return voucherDetails;
	}

	public void setVoucherDetails(List<VoucherDetailsApprv> voucherDetails) {
		this.voucherDetails = voucherDetails;
	}

	public List<PaymentDetailsO2C> getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(List<PaymentDetailsO2C> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getExternalTxnDate() {
		return externalTxnDate;
	}

	public void setExternalTxnDate(String externalTxnDate) {
		this.externalTxnDate = externalTxnDate;
	}

	public String getTransferDate() {
		return transferDate;
	}

	public void setTransferDate(String transferDate) {
		this.transferDate = transferDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRefrenceNumber() {
		return refrenceNumber;
	}

	public void setRefrenceNumber(String refrenceNumber) {
		this.refrenceNumber = refrenceNumber;
	}
	
	
	
	
	

}


class VoucherDetailsApprv {
	
	@JsonProperty("reqQuantity")
    private String reqQuantity = null;
	
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
	
	@JsonProperty("voucherProfileId")
    private String voucherProfileId = null;

	public String getVoucherProfileId() {
		return voucherProfileId;
	}
	public void setVoucherProfileId(String voucherProfileId) {
		this.voucherProfileId = voucherProfileId;
	}
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
	
	
	public String getReqQuantity() {
		return reqQuantity;
	}
	public void setReqQuantity(String reqQuantity) {
		this.reqQuantity = reqQuantity;
	}
	@Override
	public String toString() {
		return "VoucherDetails [denomination=" + denomination
				+ ", fromSerialNo=" + fromSerialNo + ", toSerialNo="
				+ toSerialNo + ", voucherType=" + voucherType
				+ ", vouchersegment=" + vouchersegment + "]";
	}

	
	
}

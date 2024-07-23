package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.Arrays;

public 	class C2CVoucherReqMessage {

	private String transferId;
	private String extnwcode;
	private VoucherDetails[] voucherDetails;
	private String status;
	private String remarks;
	private String paymentinstcode;
	private String paymentinstdate;
	private String paymentinstnum;


    

	public String getPaymentinstcode() {
		return paymentinstcode;
	}

	public void setPaymentinstcode(String paymentinstcode) {
		this.paymentinstcode = paymentinstcode;
	}

	public String getPaymentinstdate() {
		return paymentinstdate;
	}

	public void setPaymentinstdate(String paymentinstdate) {
		this.paymentinstdate = paymentinstdate;
	}

	public String getPaymentinstnum() {
		return paymentinstnum;
	}

	public void setPaymentinstnum(String paymentinstnum) {
		this.paymentinstnum = paymentinstnum;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public VoucherDetails[] getVoucherDetails() {
		return voucherDetails;
	}

	public void setVoucherDetails(VoucherDetails[] voucherDetails) {
		this.voucherDetails = voucherDetails;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getTransferId() {
		return transferId;
	}

	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}

	public String getExtnwcode() {
		return extnwcode;
	}

	public void setExtnwcode(String extnwcode) {
		this.extnwcode = extnwcode;
	}

	@Override
	public String toString() {
		return "C2CVoucherReqMessage [transferId=" + transferId + ", extnwcode=" + extnwcode + ", voucherDetails="
				+ Arrays.toString(voucherDetails) + ", status=" + status + ", remarks=" + remarks + "]";
	}

	
}

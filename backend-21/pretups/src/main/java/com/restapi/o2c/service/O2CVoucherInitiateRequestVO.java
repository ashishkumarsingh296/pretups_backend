package com.restapi.o2c.service;



import java.util.List;

import com.btsl.pretups.channel.transfer.requesthandler.PaymentDetailsO2C;
import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2CVoucherInitiateRequestVO extends OAuthUser{
	@JsonProperty("data")
    private O2CVoucherInitiateReqData o2CInitiateReqData;

    public O2CVoucherInitiateReqData getO2CInitiateReqData() {
        return o2CInitiateReqData;
    }

    public void setO2CInitiateReqData(O2CVoucherInitiateReqData data) {
        this.o2CInitiateReqData = data;
    }
    
    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
        return (sb.append("O2CVoucherInitiateReqData = ").append(o2CInitiateReqData)).toString();
    }
}

class O2CVoucherInitiateReqData {
     
	@JsonProperty("voucherDetails")
    private List<VoucherDetailsIni> voucherDetails = null;
	
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
    
	public List<VoucherDetailsIni> getVoucherDetails() {
		return voucherDetails;
	}
	public void setVoucherDetails(List<VoucherDetailsIni> voucherDetails) {
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
	@io.swagger.v3.oas.annotations.media.Schema(example = "456", required = true, defaultValue = "Reference Number")
    public String getRefnumber() {
		return refnumber;
	}
	public void setRefnumber(String refnumber) {
		this.refnumber = refnumber;
	}
	
	@JsonProperty("remarks")
	@io.swagger.v3.oas.annotations.media.Schema(example = "voucher initiate req", required = true, defaultValue = "Remarks")
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
    	StringBuilder sb = new StringBuilder();
        return (sb.append("o2CInitiateReqData = ")
        		.append("voucherDetails = ").append(voucherDetails)
        		.append("paymentdetails = ").append(paymentDetails)
        		.append("refnumber = ").append(refnumber)
        		.append("language = ").append(language)
        		.append("remarks = ").append(remarks)
        		.append("language = ").append(language)).toString();
    }
} 


class VoucherDetailsIni {
	@JsonProperty("denomination")
    private String denomination = null;
	
	@JsonProperty("quantity")
    private String quantity = null;
	
	@JsonProperty("voucherType")
    private String voucherType = null;

	@JsonProperty("vouchersegment")
    private String vouchersegment = null;

	@JsonProperty("denomination")
	@io.swagger.v3.oas.annotations.media.Schema(example = "100", required = true, defaultValue = "denomination")
    public String getDenomination() {
		return denomination;
	}
	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}
	
	@JsonProperty("quantity")
	@io.swagger.v3.oas.annotations.media.Schema(example = "2", required = true, defaultValue = "quantity")
    public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	@JsonProperty("voucherType")
	@io.swagger.v3.oas.annotations.media.Schema(example = "digital", required = true, defaultValue = "voucherType")
    public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	@JsonProperty("vouchersegment")
	@io.swagger.v3.oas.annotations.media.Schema(example = "NL", required = true, defaultValue = "vouchersegment")
    public String getVouchersegment() {
		return vouchersegment;
	}

	public void setVouchersegment(String vouchersegment) {
		this.vouchersegment = vouchersegment;
	}
	@Override
	public String toString() {
		return "VoucherDetailsVO [denomination=" + denomination + ", quantity="
				+ quantity + ", voucherType=" + voucherType
				+ ", vouchersegment=" + vouchersegment + "]";
	}

	
}


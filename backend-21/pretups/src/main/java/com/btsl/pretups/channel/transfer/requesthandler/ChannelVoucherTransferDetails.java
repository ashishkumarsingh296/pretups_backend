package com.btsl.pretups.channel.transfer.requesthandler;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChannelVoucherTransferDetails {
	
	@JsonProperty("commValue")
	private String commValue;
	@JsonProperty("tax1Value")
	private String tax1Value;
	@JsonProperty("tax2Value")
	private String tax2Value;
	@JsonProperty("tax3Value")
	private String tax3Value;
	@JsonProperty("commType")
	private String commType;
	@JsonProperty("commRate")
    private double commRate;
	@JsonProperty("tax1Type")
    private String tax1Type;
	@JsonProperty("otfTypePctOrAMt")
	private String otfTypePctOrAMt;
	@JsonProperty("cbcRate")
	private double cbcRate;
	@JsonProperty("otfTypePctOrAMt")
	public String getOtfTypePctOrAMt() {
		return otfTypePctOrAMt;
	}
	@JsonProperty("otfTypePctOrAMt")
	public void setOtfTypePctOrAMt(String otfTypePctOrAMt) {
		this.otfTypePctOrAMt = otfTypePctOrAMt;
	}
	@JsonProperty("cbcRate")
	public double getCbcRate() {
		return cbcRate;
	}
	@JsonProperty("cbcRate")
	public void setCbcRate(double cbcRate) {
		this.cbcRate = cbcRate;
	}
	@JsonProperty("cbcAmount")
	public long getCbcAmount() {
		return cbcAmount;
	}
	@JsonProperty("cbcAmount")
	public void setCbcAmount(long cbcAmount) {
		this.cbcAmount = cbcAmount;
	}

	@JsonProperty("cbcAmount")
	private long cbcAmount;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChannelVoucherTransferDetails [commValue=");
		builder.append(commValue);
		builder.append(", tax1Value=");
		builder.append(tax1Value);
		builder.append(", tax2Value=");
		builder.append(tax2Value);
		builder.append(", tax3Value=");
		builder.append(tax3Value);
		builder.append(", commType=");
		builder.append(commType);
		builder.append(", commRate=");
		builder.append(commRate);
		builder.append(", tax1Type=");
		builder.append(tax1Type);
		builder.append(", tax1Rate=");
		builder.append(tax1Rate);
		builder.append(", tax2Type=");
		builder.append(tax2Type);
		builder.append(", tax2Rate=");
		builder.append(tax2Rate);
		builder.append(", tax3Type=");
		builder.append(tax3Type);
		builder.append(", tax3Rate=");
		builder.append(tax3Rate);
		builder.append(", payableAmount=");
		builder.append(payableAmount);
		builder.append(", netPayableAmount=");
		builder.append(netPayableAmount);
		builder.append(", productCode=");
		builder.append(productCode);
		builder.append(", senderDebitQty=");
		builder.append(senderDebitQty);
		builder.append(", receiverCreditQty=");
		builder.append(receiverCreditQty);
		builder.append(", requestedQty=");
		builder.append(requestedQty);
		builder.append("]");
		return builder.toString();
	}

	@JsonProperty("tax1Rate")
    private double tax1Rate;
	@JsonProperty("tax2Type")
    private String tax2Type;
	@JsonProperty("tax2Rate")
    private double tax2Rate;
	@JsonProperty("tax3Type")
    private String tax3Type;
	@JsonProperty("tax3Rate")
    private double tax3Rate;
	@JsonProperty("payableAmount")
	private String payableAmount;
	@JsonProperty("netPayableAmount")
    private String netPayableAmount;
	
	@JsonProperty("commValue")
	public String getCommValue() {
		return commValue;
	}
	@JsonProperty("commValue")
	public void setCommValue(String commValue) {
		this.commValue = commValue;
	}
	@JsonProperty("tax1Value")
	public String getTax1Value() {
		return tax1Value;
	}
	@JsonProperty("tax1Value")
	public void setTax1Value(String tax1Value) {
		this.tax1Value = tax1Value;
	}
	@JsonProperty("tax2Value")
	public String getTax2Value() {
		return tax2Value;
	}
	@JsonProperty("tax2Value")
	public void setTax2Value(String tax2Value) {
		this.tax2Value = tax2Value;
	}
	@JsonProperty("tax3Value")
	public String getTax3Value() {
		return tax3Value;
	}
	@JsonProperty("tax3Value")
	public void setTax3Value(String tax3Value) {
		this.tax3Value = tax3Value;
	}
	@JsonProperty("commType")
	public String getCommType() {
		return commType;
	}
	@JsonProperty("commType")
	public void setCommType(String commType) {
		this.commType = commType;
	}
	@JsonProperty("commRate")
	public double getCommRate() {
		return commRate;
	}
	@JsonProperty("commRate")
	public void setCommRate(double commRate) {
		this.commRate = commRate;
	}
	@JsonProperty("tax1Type")
	public String getTax1Type() {
		return tax1Type;
	}
	@JsonProperty("tax1Type")
	public void setTax1Type(String tax1Type) {
		this.tax1Type = tax1Type;
	}
	@JsonProperty("tax1Rate")
	public double getTax1Rate() {
		return tax1Rate;
	}
	@JsonProperty("tax1Rate")
	public void setTax1Rate(double tax1Rate) {
		this.tax1Rate = tax1Rate;
	}
	@JsonProperty("tax2Type")
	public String getTax2Type() {
		return tax2Type;
	}
	@JsonProperty("tax2Type")
	public void setTax2Type(String tax2Type) {
		this.tax2Type = tax2Type;
	}
	@JsonProperty("tax2Rate")
	public double getTax2Rate() {
		return tax2Rate;
	}
	@JsonProperty("tax2Rate")
	public void setTax2Rate(double tax2Rate) {
		this.tax2Rate = tax2Rate;
	}
	@JsonProperty("tax3Type")
	public String getTax3Type() {
		return tax3Type;
	}
	@JsonProperty("tax3Type")
	public void setTax3Type(String tax3Type) {
		this.tax3Type = tax3Type;
	}
	@JsonProperty("tax3Rate")
	public double getTax3Rate() {
		return tax3Rate;
	}
	@JsonProperty("tax3Rate")
	public void setTax3Rate(double tax3Rate) {
		this.tax3Rate = tax3Rate;
	}
	@JsonProperty("payableAmount")
	public String getPayableAmount() {
		return payableAmount;
	}
	@JsonProperty("payableAmount")
	public void setPayableAmount(String payableAmount) {
		this.payableAmount = payableAmount;
	}
	@JsonProperty("netPayableAmount")
	public String getNetPayableAmount() {
		return netPayableAmount;
	}
	@JsonProperty("netPayableAmount")
	public void setNetPayableAmount(String netPayableAmount) {
		this.netPayableAmount = netPayableAmount;
	}
	@JsonProperty("productCode")
	public String getProductCode() {
		return productCode;
	}
	@JsonProperty("productCode")
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	@JsonProperty("senderDebitQty")
	public String getSenderDebitQty() {
		return senderDebitQty;
	}
	@JsonProperty("senderDebitQty")
	public void setSenderDebitQty(String senderDebitQty) {
		this.senderDebitQty = senderDebitQty;
	}
	@JsonProperty("receiverCreditQty")
	public String getReceiverCreditQty() {
		return receiverCreditQty;
	}
	@JsonProperty("receiverCreditQty")
	public void setReceiverCreditQty(String receiverCreditQty) {
		this.receiverCreditQty = receiverCreditQty;
	}
	@JsonProperty("requestedQty")
	public String getRequestedQty() {
		return requestedQty;
	}
	@JsonProperty("requestedQty")
	public void setRequestedQty(String requestedQty) {
		this.requestedQty = requestedQty;
	}

	@JsonProperty("productCode")
    private String productCode;
	@JsonProperty("senderDebitQty")
    private String senderDebitQty;
	@JsonProperty("receiverCreditQty")
    private String receiverCreditQty;
	@JsonProperty("requestedQty")
    private String requestedQty;
}

package com.btsl.gateway.razorpay;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OnlinePaymentRequestVO extends OAuthUser{
	@JsonProperty("paymentId")
    private String paymentId;
	
	@JsonProperty("orderId")
    private String orderId;
	
	@JsonProperty("signature")
    private String signature;
	
	@JsonProperty("transferId")
    private String transferID;
	
	@JsonProperty("paymentGatewayStatus")
    private String paymentGatewayStatus;

	public String getTransferID() {
		return transferID;
	}

	public void setTransferID(String transferID) {
		this.transferID = transferID;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getPaymentGatewayStatus() {
		return paymentGatewayStatus;
	}

	public void setPaymentGatewayStatus(String paymentGatewayStatus) {
		this.paymentGatewayStatus = paymentGatewayStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OnlinePaymentRequestVO [paymentId=");
		builder.append(paymentId);
		builder.append(", orderId=");
		builder.append(orderId);
		builder.append(", signature=");
		builder.append(signature);
		builder.append(", transferID=");
		builder.append(transferID);
		builder.append(", paymentGatewayStatus=");
		builder.append(paymentGatewayStatus);
		builder.append("]");
		return builder.toString();
	}
}

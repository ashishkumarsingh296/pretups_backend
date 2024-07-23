package com.btsl.pretups.channel.transfer.requesthandler;

import com.btsl.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public class O2cInitiateResponseVO extends BaseResponse {

	@JsonProperty("gatewayOrderId")
	private String gatewayOrderId;
	
	@JsonProperty("onlinePayment")
	private boolean onlinePayment = false;

	public String getGatewayOrderId() {
		return gatewayOrderId;
	}

	public void setGatewayOrderId(String gatewayOrderId) {
		this.gatewayOrderId = gatewayOrderId;
	}

	public boolean isOnlinePayment() {
		return onlinePayment;
	}

	public void setOnlinePayment(boolean onlinePayment) {
		this.onlinePayment = onlinePayment;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2cInitiateResponseVO [gatewayOrderId=");
		builder.append(gatewayOrderId);
		builder.append(", onlinePayment=");
		builder.append(onlinePayment);
		builder.append("]");
		return builder.toString();
	}
	

}

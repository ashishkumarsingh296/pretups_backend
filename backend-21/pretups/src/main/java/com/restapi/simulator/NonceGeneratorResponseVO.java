package com.restapi.simulator;

import com.btsl.common.BaseResponse;

public class NonceGeneratorResponseVO extends BaseResponse{

	String nonce;
	String signature;
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NonceGeneratorResponseVO [nonce=");
		builder.append(nonce);
		builder.append(", signature=");
		builder.append(signature);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}

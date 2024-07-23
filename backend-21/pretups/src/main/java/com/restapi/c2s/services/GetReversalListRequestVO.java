package com.restapi.c2s.services;

import com.btsl.user.businesslogic.OAuthUser;
import com.fasterxml.jackson.annotation.JsonProperty;



public class GetReversalListRequestVO {
	
	@JsonProperty("senderMsisdn")
	String senderMsisdn;
	
	@JsonProperty("receiverMsisdn")
	String receiverMsisdn;
	
	@JsonProperty("txnID")
	String txnID;

	@JsonProperty("senderMsisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "72525252",required = true/* , defaultValue = "" */, description="Msisdn of Sender")
	public String getSenderMsisdn() {
		return senderMsisdn;
	}

	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}

	@JsonProperty("receiverMsisdn")
	@io.swagger.v3.oas.annotations.media.Schema(example = "723000000",required = true/* , defaultValue = "" */, description="Msisdn of Receiver")
	public String getReceiverMsisdn() {
		return receiverMsisdn;
	}

	public void setReceiverMsisdn(String receiverMsisdn) {
		this.receiverMsisdn = receiverMsisdn;
	}

	@JsonProperty("txnID")
	@io.swagger.v3.oas.annotations.media.Schema(example = "R2019.2345.213",required = true/* , defaultValue = "" */, description="Txn ID")
	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("GetReversalListRequestVO [senderMsisdn=").append(senderMsisdn).append(", receiverMsisdn")
		.append(receiverMsisdn).append(", txnID=").append(txnID);
		
		return sb.toString();
	}
	
	
	
}

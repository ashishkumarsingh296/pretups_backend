package restassuredapi.pojo.loadreversallistresquestpojo;

import com.fasterxml.jackson.annotation.JsonProperty;


public class LoadReversalListRequestPojo {
	
	@JsonProperty("senderMsisdn")
	String senderMsisdn;
	
	@JsonProperty("receiverMsisdn")
	String receiverMsisdn;
	
	@JsonProperty("txnID")
	String txnID;

	@JsonProperty("senderMsisdn")
	public String getSenderMsisdn() {
		return senderMsisdn;
	}

	public void setSenderMsisdn(String senderMsisdn) {
		this.senderMsisdn = senderMsisdn;
	}

	@JsonProperty("receiverMsisdn")
	public String getReceiverMsisdn() {
		return receiverMsisdn;
	}

	public void setReceiverMsisdn(String receiverMsisdn) {
		this.receiverMsisdn = receiverMsisdn;
	}

	@JsonProperty("txnID")
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

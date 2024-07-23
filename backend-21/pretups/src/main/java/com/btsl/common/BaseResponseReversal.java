package com.btsl.common;

public class BaseResponseReversal extends BaseResponse {
	
	public String txnid;
	public String getTxnid() {
		return txnid;
	}
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	public String getReceiverTrfValue() {
		return receiverTrfValue;
	}
	public void setReceiverTrfValue(String receiverTrfValue) {
		this.receiverTrfValue = receiverTrfValue;
	}
	public String getReceiveraccessValue() {
		return receiveraccessValue;
	}
	public void setReceiveraccessValue(String receiveraccessValue) {
		this.receiveraccessValue = receiveraccessValue;
	}
	public String receiverTrfValue;
	public String receiveraccessValue;
	 @Override
	    public String toString() {
	    	StringBuilder sb = new StringBuilder();
	        return (sb.append("service = ").append(txnid)
	        		.append("referenceId").append( receiverTrfValue)
	        		.append("status").append( receiveraccessValue)
	        		).toString();
	    }
	

}

package com.restapi.channelAdmin.requestVO;



public class O2CTxnReversalRequestVO {
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "OT220202.0107.720001", required = true/* , defaultValue = "" */)
	private String transactionID;
	
	@io.swagger.v3.oas.annotations.media.Schema(example = "Test", required = true/* , defaultValue = "" */)
	private String remarks;

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CTxnReversalRequestVO [transactionID=");
		builder.append(transactionID);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append("]");
		return builder.toString();
	}

	
	
	

}

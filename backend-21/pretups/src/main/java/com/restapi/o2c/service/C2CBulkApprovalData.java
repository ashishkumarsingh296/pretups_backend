package com.restapi.o2c.service;

public class C2CBulkApprovalData {
	
	String txnID;
	
	String approvalStatus;
	
	String remarks;
	
	String txnType;
	
	String language1;
	
	String language2;
	
	
	public String getLanguage1() {
		return language1;
	}

	public void setLanguage1(String language1) {
		this.language1 = language1;
	}

	public String getLanguage2() {
		return language2;
	}

	public void setLanguage2(String language2) {
		this.language2 = language2;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnID() {
		return txnID;
	}

	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}



	public String getApprovalStatus() {
		return approvalStatus;
	}



	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
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
		builder.append("C2CBulkApprovalRequestVO [txnID=");
		builder.append(txnID);
		builder.append(", approvalStatus=");
		builder.append(approvalStatus);
		builder.append(", remarks=");
		builder.append(remarks);
		builder.append("]");
		return builder.toString();
	}
	
	

}

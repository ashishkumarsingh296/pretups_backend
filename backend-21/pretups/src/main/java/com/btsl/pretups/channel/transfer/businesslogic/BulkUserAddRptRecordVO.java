package com.btsl.pretups.channel.transfer.businesslogic;

public class BulkUserAddRptRecordVO {
	
	private String batchNo;
	private String batchStatus;
	private String totalRecords;
	private String newRecords;
	private String activeRecords;
	private String rejectedRecords;
	
	
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getBatchStatus() {
		return batchStatus;
	}
	public void setBatchStatus(String batchStatus) {
		this.batchStatus = batchStatus;
	}
	public String getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getNewRecords() {
		return newRecords;
	}
	public void setNewRecords(String newRecords) {
		this.newRecords = newRecords;
	}
	public String getActiveRecords() {
		return activeRecords;
	}
	public void setActiveRecords(String activeRecords) {
		this.activeRecords = activeRecords;
	}
	public String getRejectedRecords() {
		return rejectedRecords;
	}
	public void setRejectedRecords(String rejectedRecords) {
		this.rejectedRecords = rejectedRecords;
	}
	
}

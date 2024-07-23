package com.restapi.channel.transfer.channelvoucherapproval;

import com.restapi.user.service.FileDownloadResponse;

public class DownloadBatchTxnWdrResponse extends FileDownloadResponse {

	String batchNumber;
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getTotalTransfers() {
		return totalTransfers;
	}
	public void setTotalTransfers(String totalTransfers) {
		this.totalTransfers = totalTransfers;
	}
	public String getNewTransfers() {
		return newTransfers;
	}
	public void setNewTransfers(String newTransfers) {
		this.newTransfers = newTransfers;
	}
	public String getApprovedTransfers() {
		return approvedTransfers;
	}
	public void setApprovedTransfers(String approvedTransfers) {
		this.approvedTransfers = approvedTransfers;
	}
	public String getClosedTransfers() {
		return closedTransfers;
	}
	public void setClosedTransfers(String closedTransfers) {
		this.closedTransfers = closedTransfers;
	}
	public String getRejectedTransfers() {
		return rejectedTransfers;
	}
	public void setRejectedTransfers(String rejectedTransfers) {
		this.rejectedTransfers = rejectedTransfers;
	}
	String batchName;
	@Override
	public String toString() {
		return "DownloadBatchTxnWdrResponse [batchNumber=" + batchNumber + ", batchName=" + batchName + ", product="
				+ product + ", totalTransfers=" + totalTransfers + ", newTransfers=" + newTransfers
				+ ", approvedTransfers=" + approvedTransfers + ", closedTransfers=" + closedTransfers
				+ ", rejectedTransfers=" + rejectedTransfers + "]";
	}
	String product;
	String totalTransfers;
	String newTransfers;
	String approvedTransfers;
	String closedTransfers;
	String rejectedTransfers;
}

package com.restapi.c2s.services;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;

public class DvdBulkResponse extends BaseResponseMultiple{
	private String fileName;
	private String fileAttachment;
	private String txnBatchId;
	private List<TxnIDBaseResponse> txnDetailsList;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileAttachment() {
		return fileAttachment;
	}
	public void setFileAttachment(String fileAttachment) {
		this.fileAttachment = fileAttachment;
	}
	public String getTxnBatchId() {
		return txnBatchId;
	}
	public void setTxnBatchId(String txnBatchId) {
		this.txnBatchId = txnBatchId;
	}
	
	public List<TxnIDBaseResponse> getTxnDetailsList() {
		return txnDetailsList;
	}
	public void setTxnDetailsList(List<TxnIDBaseResponse> txnDetailsList) {
		this.txnDetailsList = txnDetailsList;
	}
	@Override
	public String toString() {
		return "DvdBulkResponse [fileName=" + fileName + ", fileAttachment=" + fileAttachment + ", txnBatchId="
				+ txnBatchId + ", txnDetailsList=" + txnDetailsList + "]";
	}
	
	
}

package com.restapi.c2s.services;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;

public class DvdApiResponse extends BaseResponseMultiple{
	private String txnBatchId;
	private List<TxnIDBaseResponse> txnDetailsList;
	
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
		return "DvdApiResponse [txnBatchId=" + txnBatchId + ", txnDetailsList=" + txnDetailsList + "]";
	}
	
	
	

}

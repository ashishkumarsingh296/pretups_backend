package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class DisplayStockTxnDetailsViewResponseVO extends BaseResponse{
	
	private String totalMrpStr;
	private ArrayList stockItemsList = null;
	
	//extra feilds used for next api's in this flow 
	private String stockType;
	private String entryType;
	private String txnType;
	private String txnNo;
	private String requesterName;
	private String stockDateStr;
	private String referenceNumber;
	private String txnStatusDesc;
	private String networkForName;
	private String remarks;
	private String networkCodeFor;
	private long lastModifiedTime =  0L;
	private String firstLevelRemarks;
	private String firstLevelApprovedBy;
	private String secondLevelRemarks;
	private String secondLevelApprovedBy;
	private String walletType;
	
	
	public String getTotalMrpStr() {
		return totalMrpStr;
	}
	public void setTotalMrpStr(String totalMrpStr) {
		this.totalMrpStr = totalMrpStr;
	}
	public ArrayList getStockItemsList() {
		return stockItemsList;
	}
	public void setStockItemsList(ArrayList stockItemsList) {
		this.stockItemsList = stockItemsList;
	}
	public String getStockType() {
		return stockType;
	}
	public void setStockType(String stockType) {
		this.stockType = stockType;
	}
	public String getEntryType() {
		return entryType;
	}
	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getTxnNo() {
		return txnNo;
	}
	public void setTxnNo(String txnNo) {
		this.txnNo = txnNo;
	}
	public String getRequesterName() {
		return requesterName;
	}
	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}
	public String getStockDateStr() {
		return stockDateStr;
	}
	public void setStockDateStr(String stockDateStr) {
		this.stockDateStr = stockDateStr;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getTxnStatusDesc() {
		return txnStatusDesc;
	}
	public void setTxnStatusDesc(String txnStatusDesc) {
		this.txnStatusDesc = txnStatusDesc;
	}
	public String getNetworkForName() {
		return networkForName;
	}
	public void setNetworkForName(String networkForName) {
		this.networkForName = networkForName;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getNetworkCodeFor() {
		return networkCodeFor;
	}
	public void setNetworkCodeFor(String networkCodeFor) {
		this.networkCodeFor = networkCodeFor;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public String getFirstLevelRemarks() {
		return firstLevelRemarks;
	}
	public void setFirstLevelRemarks(String firstLevelRemarks) {
		this.firstLevelRemarks = firstLevelRemarks;
	}
	public String getFirstLevelApprovedBy() {
		return firstLevelApprovedBy;
	}
	public void setFirstLevelApprovedBy(String firstLevelApprovedBy) {
		this.firstLevelApprovedBy = firstLevelApprovedBy;
	}
	public String getSecondLevelRemarks() {
		return secondLevelRemarks;
	}
	public void setSecondLevelRemarks(String secondLevelRemarks) {
		this.secondLevelRemarks = secondLevelRemarks;
	}
	public String getSecondLevelApprovedBy() {
		return secondLevelApprovedBy;
	}
	public void setSecondLevelApprovedBy(String secondLevelApprovedBy) {
		this.secondLevelApprovedBy = secondLevelApprovedBy;
	}
	public String getWalletType() {
		return walletType;
	}
	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}
	
	
	
}

package com.restapi.networkadmin.requestVO;

import java.util.ArrayList;

import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;

public class AddStockRequestVO {

	private String networkCode;
	private String requesterName;
	private String userID;
	private String entryType;
	private String txnType;
	private String txnStatus;
	private String stockDateStr;
	
	private String networkCodeFor;
	private String networkForName;
	private String networkName;
	private String stockType;


	
	private long totalMrp = 0L;
	private String totalMrpStr;
	private long maxAmountLimit = 0L;
	private double totalQty = 0D;
	
	private String fromDateStr;
    private String toDateStr;
    
    private String remarks;
    private String referenceNumber;
    
    private long lastModifiedTime = 0L;
    private long firstLevelAppLimit = 0L;
    
    private String walletType;
    
    ArrayList<NetworkStockTxnItemsVO> stockProductList = new ArrayList<>();
    
	
	public ArrayList<NetworkStockTxnItemsVO> getStockProductList() {
		return stockProductList;
	}
	public void setStockProductList(ArrayList<NetworkStockTxnItemsVO> stockProductList) {
		this.stockProductList = stockProductList;
	}
	
	public String getWalletType() {
		return walletType;
	}
	
	public void setWalletType(String walletType) {
		this.walletType = walletType;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getRequesterName() {
		return requesterName;
	}
	public void setRequesterName(String requesterName) {
		this.requesterName = requesterName;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
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
	public String getTxnStatus() {
		return txnStatus;
	}
	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}
	public String getStockDateStr() {
		return stockDateStr;
	}
	public void setStockDateStr(String stockDateStr) {
		this.stockDateStr = stockDateStr;
	}
	public String getNetworkCodeFor() {
		return networkCodeFor;
	}
	public void setNetworkCodeFor(String networkCodeFor) {
		this.networkCodeFor = networkCodeFor;
	}
	public String getNetworkForName() {
		return networkForName;
	}
	public void setNetworkForName(String networkForName) {
		this.networkForName = networkForName;
	}
	public String getNetworkName() {
		return networkName;
	}
	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}
	public String getStockType() {
		return stockType;
	}
	public void setStockType(String stockType) {
		this.stockType = stockType;
	}
	
	public long getTotalMrp() {
		return totalMrp;
	}
	public void setTotalMrp(long totalMrp) {
		this.totalMrp = totalMrp;
	}
	public String getTotalMrpStr() {
		return totalMrpStr;
	}
	public void setTotalMrpStr(String totalMrpStr) {
		this.totalMrpStr = totalMrpStr;
	}
	public long getMaxAmountLimit() {
		return maxAmountLimit;
	}
	public void setMaxAmountLimit(long maxAmountLimit) {
		this.maxAmountLimit = maxAmountLimit;
	}
	public double getTotalQty() {
		return totalQty;
	}
	public void setTotalQty(double totalQty) {
		this.totalQty = totalQty;
	}
	public String getFromDateStr() {
		return fromDateStr;
	}
	public void setFromDateStr(String fromDateStr) {
		this.fromDateStr = fromDateStr;
	}
	public String getToDateStr() {
		return toDateStr;
	}
	public void setToDateStr(String toDateStr) {
		this.toDateStr = toDateStr;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public long getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public long getFirstLevelAppLimit() {
		return firstLevelAppLimit;
	}
	public void setFirstLevelAppLimit(long firstLevelAppLimit) {
		this.firstLevelAppLimit = firstLevelAppLimit;
	}
    
    
	
}
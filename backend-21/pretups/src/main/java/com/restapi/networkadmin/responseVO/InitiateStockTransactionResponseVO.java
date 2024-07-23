package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class InitiateStockTransactionResponseVO extends BaseResponse{
	
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

	private ArrayList stockProductList = null;

	
	
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

	public ArrayList getStockProductList() {
		return stockProductList;
	}

	public void setStockProductList(ArrayList stockProductList) {
		this.stockProductList = stockProductList;
	}
	
	
	
}

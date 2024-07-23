package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class LevelOneApprovalListResponseVO extends BaseResponse{
	
	private ArrayList stockTxnList = null;
	private String userID;
	private String networkCode;
	

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}

	public ArrayList getStockTxnList() {
		return stockTxnList;
	}

	public void setStockTxnList(ArrayList stockTxnList) {
		this.stockTxnList = stockTxnList;
	}
	
	
}

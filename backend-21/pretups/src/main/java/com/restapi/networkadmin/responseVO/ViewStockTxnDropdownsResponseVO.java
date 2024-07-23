package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class ViewStockTxnDropdownsResponseVO extends BaseResponse{
	
	private ArrayList statusList = null;
    private ArrayList stockTypeList = null;
    private String fromDateStr;
    private String toDateStr;
    private ArrayList roamNetworkList = null;
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

	public ArrayList getStatusList() {
		return statusList;
	}

	public void setStatusList(ArrayList statusList) {
		this.statusList = statusList;
	}

	public ArrayList getStockTypeList() {
		return stockTypeList;
	}

	public void setStockTypeList(ArrayList stockTypeList) {
		this.stockTypeList = stockTypeList;
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

	public ArrayList getRoamNetworkList() {
		return roamNetworkList;
	}

	public void setRoamNetworkList(ArrayList roamNetworkList) {
		this.roamNetworkList = roamNetworkList;
	}
    
    
    
}

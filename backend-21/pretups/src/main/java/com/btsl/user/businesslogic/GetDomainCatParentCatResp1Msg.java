package com.btsl.user.businesslogic;

import java.util.ArrayList;

public class GetDomainCatParentCatResp1Msg {
	String categoryName;
	String categoryNameCode;
	int maxTxnMsisdn;
	public ArrayList<GetDomainCatParentCatResp2Msg>parentList;
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public ArrayList<GetDomainCatParentCatResp2Msg> getParentList() {
		return parentList;
	}
	public void setParentList(ArrayList<GetDomainCatParentCatResp2Msg> parentList) {
		this.parentList = parentList;
	}
	public String getCategoryNameCode() {
		return categoryNameCode;
	}
	public void setCategoryNameCode(String categoryNameCode) {
		this.categoryNameCode = categoryNameCode;
	}
	public int getMaxTxnMsisdn() {
		return maxTxnMsisdn;
	}
	public void setMaxTxnMsisdn(int maxTxnMsisdn) {
		this.maxTxnMsisdn = maxTxnMsisdn;
	}
	
	
	

}

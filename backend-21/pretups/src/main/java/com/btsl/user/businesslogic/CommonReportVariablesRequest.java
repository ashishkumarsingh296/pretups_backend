package com.btsl.user.businesslogic;

import java.util.ArrayList;

public class CommonReportVariablesRequest {

	private ArrayList<LOVList> lOVList;
	private String componetsglobalId;
	private ArrayList<Param> commonParams;
	
	
	public String getComponetsglobalId() {
		return componetsglobalId;
	}

	public void setComponetsglobalId(String componetsglobalId) {
		this.componetsglobalId = componetsglobalId;
	}

	public ArrayList<LOVList> getlOVList() {
		return lOVList;
	}

	public void setlOVList(ArrayList<LOVList> lOVList) {
		this.lOVList = lOVList;
	}

	public ArrayList<Param> getCommonParams() {
		return commonParams;
	}

	public void setCommonParams(ArrayList<Param> commonParams) {
		this.commonParams = commonParams;
	}

}

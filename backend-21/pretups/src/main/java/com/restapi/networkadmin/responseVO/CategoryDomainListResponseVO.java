package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class CategoryDomainListResponseVO extends BaseResponse{
	private ArrayList categoryDomainList = null;
	private String type = null;
	private String networkCode = null;
	private String networkDescription = null;
	private String userCategory = null;
	
	
	
	public ArrayList getCategoryDomainList() {
		return categoryDomainList;
	}
	public void setCategoryDomainList(ArrayList categoryDomainList) {
		this.categoryDomainList = categoryDomainList;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNetworkCode() {
		return networkCode;
	}
	public void setNetworkCode(String networkCode) {
		this.networkCode = networkCode;
	}
	public String getNetworkDescription() {
		return networkDescription;
	}
	public void setNetworkDescription(String networkDescription) {
		this.networkDescription = networkDescription;
	}
	public String getUserCategory() {
		return userCategory;
	}
	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}
	
	
}

package com.btsl.user.businesslogic;

import java.util.ArrayList;

import com.btsl.pretups.channel.transfer.businesslogic.GetDomainCategoryMsg;

public class GetDomainCategoryResponseVO {
	
	String domainCode;
	String domainCodeName;
	public ArrayList<GetDomainCategoryMsg>categoryList;
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getDomainCodeName() {
		return domainCodeName;
	}
	public void setDomainCodeName(String domainCodeName) {
		this.domainCodeName = domainCodeName;
	}
	public ArrayList<GetDomainCategoryMsg> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<GetDomainCategoryMsg> categoryList) {
		this.categoryList = categoryList;
	}
	
	
	
	
	
	
	

}

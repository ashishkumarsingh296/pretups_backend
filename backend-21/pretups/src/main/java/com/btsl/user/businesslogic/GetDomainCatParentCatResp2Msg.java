package com.btsl.user.businesslogic;

import java.util.ArrayList;

import com.btsl.pretups.channel.transfer.requesthandler.GetDomainCatParentCatParentUserMsg;

public class GetDomainCatParentCatResp2Msg {
	String parentCategory;
	String parentCategoryCode;
	public ArrayList<GetDomainCatParentCatParentUserMsg>parentUser;
	
	public String getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(String parentCategory) {
		this.parentCategory = parentCategory;
	}
	public ArrayList<GetDomainCatParentCatParentUserMsg> getParentUser() {
		return parentUser;
	}
	public void setParentUser(ArrayList<GetDomainCatParentCatParentUserMsg> parentUser) {
		this.parentUser = parentUser;
	}
	public String getParentCategoryCode() {
		return parentCategoryCode;
	}
	public void setParentCategoryCode(String parentCategoryCode) {
		this.parentCategoryCode = parentCategoryCode;
	}
	
	

}

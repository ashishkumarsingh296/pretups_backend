package com.btsl.pretups.channel.transfer.requesthandler;

public class GetDomainCatParentCat {

	
	String parentCategoryName;

	public String getParentCategoryName() {
		return parentCategoryName;
	}

	public void setParentCategoryName(String parentCategoryName) {
		this.parentCategoryName = parentCategoryName;
	}
	@Override
	public String toString() {
		return "{parentCategoryName=" + parentCategoryName + "}";
	}
	

}

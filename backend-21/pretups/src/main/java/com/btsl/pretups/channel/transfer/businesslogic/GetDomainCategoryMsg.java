package com.btsl.pretups.channel.transfer.businesslogic;

public class GetDomainCategoryMsg {
	
	String categoryCode;
	
	String categoryName;

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString() {
		return "{categoryCode=" + categoryCode + ", categoryName=" + categoryName + "}";
	}
	
	
	

}

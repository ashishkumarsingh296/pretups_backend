package com.btsl.pretups.channel.transfer.requesthandler;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class CategoryListResponseVO extends BaseResponse {
	private ArrayList categoryList;

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	
	@Override
	public String toString() {
		return "CategoryList [CategoryList=" + categoryList;
	}
}

	

package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class CategoryListRespVO  extends BaseResponse{
 
	public ArrayList categoryList;


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CategoryListRespVO [categoryList=");
		builder.append(categoryList);
		builder.append("]");
		return builder.toString();
	}

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
}

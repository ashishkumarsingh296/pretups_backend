package com.btsl.common;

import java.util.ArrayList;

public class CategoryRespVO extends BaseResponse{
	
	ArrayList<CategoryData> catList = null;

	public ArrayList<CategoryData> getCatList() {
		return catList;
	}

	public void setCatList(ArrayList<CategoryData> catList) {
		this.catList = catList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CategoryRespVO [catList=");
		builder.append(catList);
		builder.append("]");
		return builder.toString();
	}
	
	

}

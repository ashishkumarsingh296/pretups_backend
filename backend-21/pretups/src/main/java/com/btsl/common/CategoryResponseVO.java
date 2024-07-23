package com.btsl.common;

import java.util.ArrayList;

public class CategoryResponseVO extends BaseResponse{
	
	ArrayList catList = null;

	public ArrayList getCatList() {
		return catList;
	}

	public void setCatList(ArrayList catList) {
		this.catList = catList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CategoryResponseVO [catList=");
		builder.append(catList);
		builder.append("]");
		return builder.toString();
	}
	
	

}

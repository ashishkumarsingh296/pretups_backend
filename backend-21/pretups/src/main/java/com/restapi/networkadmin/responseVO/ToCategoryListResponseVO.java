package com.restapi.networkadmin.responseVO;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class ToCategoryListResponseVO extends BaseResponse{
	private ArrayList categoryList = null;
	private ArrayList productList = null;
	
	public ArrayList getProductList() {
		return productList;
	}

	public void setProductList(ArrayList productList) {
		this.productList = productList;
	}

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}
	
	
}

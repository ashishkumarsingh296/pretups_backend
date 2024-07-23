package com.restapi.networkadmin.loanmanagment;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class LoanProductListResponseVO extends BaseResponse {
	private ArrayList productList;

	public ArrayList getProductList() {
		return productList;
	}

	public void setProductList(ArrayList productList) {
		this.productList = productList;
	}
}

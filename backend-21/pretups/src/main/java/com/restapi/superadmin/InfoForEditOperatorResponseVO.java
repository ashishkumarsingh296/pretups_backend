package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class InfoForEditOperatorResponseVO extends BaseResponse {

	public ArrayList domainList;

	public ArrayList serivesList;

	public ArrayList productList;

	public ArrayList voucherTypeList;

	public ArrayList voucherSegmentList;

	public ArrayList getDomainList() {
		return domainList;
	}

	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
	}

	public ArrayList getSerivesList() {
		return serivesList;
	}

	public void setSerivesList(ArrayList serivesList) {
		this.serivesList = serivesList;
	}

	public ArrayList getProductList() {
		return productList;
	}

	public void setProductList(ArrayList productList) {
		this.productList = productList;
	}

	public ArrayList getVoucherTypeList() {
		return voucherTypeList;
	}

	public void setVoucherTypeList(ArrayList voucherTypeList) {
		this.voucherTypeList = voucherTypeList;
	}

	public ArrayList getVoucherSegmentList() {
		return voucherSegmentList;
	}

	public void setVoucherSegmentList(ArrayList voucherSegmentList) {
		this.voucherSegmentList = voucherSegmentList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InfoForEditOperatorResponseVO [domainList=");
		builder.append(domainList);
		builder.append(", serivesList=");
		builder.append(serivesList);
		builder.append(", productList=");
		builder.append(productList);
		builder.append(", voucherTypeList=");
		builder.append(voucherTypeList);
		builder.append(", voucherSegmentList=");
		builder.append(voucherSegmentList);
		builder.append("]");
		return builder.toString();
	}

}

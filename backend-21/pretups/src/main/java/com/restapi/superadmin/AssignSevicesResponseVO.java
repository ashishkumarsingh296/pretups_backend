package com.restapi.superadmin;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

public class AssignSevicesResponseVO extends BaseResponse {

	public ArrayList domainList;

	public ArrayList productList;

	public ArrayList voucherTypeList;

	public ArrayList voucherSegmentList;

	public ArrayList servicesList;

	public ArrayList getDomainList() {
		return domainList;
	}

	public void setDomainList(ArrayList domainList) {
		this.domainList = domainList;
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

	public ArrayList getServicesList() {
		return servicesList;
	}

	public void setServicesList(ArrayList servicesList) {
		this.servicesList = servicesList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssignSevicesResponseVO [domainList=");
		builder.append(domainList);
		builder.append(", productList=");
		builder.append(productList);
		builder.append(", voucherTypeList=");
		builder.append(voucherTypeList);
		builder.append(", voucherSegmentList=");
		builder.append(voucherSegmentList);
		builder.append(", servicesList=");
		builder.append(servicesList);
		builder.append("]");
		return builder.toString();
	}

}

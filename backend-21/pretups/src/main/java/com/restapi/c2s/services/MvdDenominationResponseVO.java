package com.restapi.c2s.services;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;



public class MvdDenominationResponseVO extends BaseResponseMultiple {
	
	
	 ArrayList<MvdResponseData> voucherDenomList=new ArrayList<>();

	public ArrayList<MvdResponseData> getVoucherDenomList() {
		return voucherDenomList;
	}

	public void setVoucherDenomList(ArrayList<MvdResponseData> voucherDenomList) {
		this.voucherDenomList = voucherDenomList;
	}



}
